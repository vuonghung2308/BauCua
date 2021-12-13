package vn.vm.baucua.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import vn.vm.baucua.data.entity.Bet;
import vn.vm.baucua.data.entity.ChatMessage;
import vn.vm.baucua.data.entity.RankRow;
import vn.vm.baucua.data.entity.PersonalInfo;
import vn.vm.baucua.data.entity.User;
import vn.vm.baucua.data.request.ForgotPasswordRequest;
import vn.vm.baucua.data.request.GoRoomRequest;
import vn.vm.baucua.data.request.LoginRequest;
import vn.vm.baucua.data.request.RegisterRequest;
import vn.vm.baucua.data.request.Request;
import vn.vm.baucua.data.response.Response;
import vn.vm.baucua.database.dao.UserDao;
import vn.vm.baucua.socket.pool.ClientPool;
import vn.vm.baucua.socket.pool.RoomPool;
import vn.vm.baucua.util.KeyForgotUtil;
import vn.vm.baucua.util.StroUtils;
import vn.vm.baucua.util.Log;
import vn.vm.baucua.util.ThreadUtils;

public class Client {

    private final Socket socket;
    private ClientPool clientPool;
    private DataOutputStream dos;
    private DataInputStream dis;
    private RoomPool roomPool;
    private UserDao userDao;
    private OutputStream os;
    private InputStream is;
    private Timer timer;
    private Room room;
    private User user;
    private KeyForgotUtil forgotUtil;

    public Client(Socket socket) {
        this.socket = socket;
        schedulePing();
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        if (user == null) {
            return -1;
        }
        return user.id;
    }

    public void initSocket() throws IOException {
        clientPool = ClientPool.getInstance();
        roomPool = RoomPool.getInstance();
        is = socket.getInputStream();
        os = socket.getOutputStream();
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);
        userDao = new UserDao();
        forgotUtil = new KeyForgotUtil();
    }

    public void closeSocket() throws IOException {
        dos.close();
        dis.close();
        is.close();
        os.close();
        socket.close();
        if (timer != null) {
            timer.cancel();
        }
    }

    public Request receive() throws IOException {
        byte[] data = new byte[5048];
        int bytes = dis.read(data);
        if (bytes < 0) {
            throw new IOException("connection closed");
        }

        schedulePing();
        String jsonRequest = new String(
                data, 0, bytes,
                StandardCharsets.UTF_8
        );

        printLog(jsonRequest, true);

        Request request = new Request(jsonRequest);
        if (request.content == null) {
            Response response = Response.error(
                    300, request, "no content"
            );
            send(response);
            return null;
        }
        schedulePing();
        return request;
    }

    public void send(Response response) {
        try {
            String responseJson = StroUtils.toStro(response);
            String mesage = responseJson + "<EOF>";
            dos.write(mesage.getBytes(StandardCharsets.UTF_8));

            printLog(responseJson, false);
            schedulePing();
            dos.flush();

        } catch (IOException e) {
            Log.e(e);
        }
    }

    public boolean handleLoginRequest(Request request) throws IOException {
        LoginRequest data = (LoginRequest) request.getDataObject();
        User newUser = userDao.getUser(data.username, data.password, data.email);
        if (newUser != null) {
            Client c = clientPool.getClient(newUser.id);
            if (c != null) {
                sendError(request, 401, "Tài khoản đang được đăng nhập,"
                        + " vui lòng kiểm tra lại.");
                return false;
            }
            this.user = newUser;
            clientPool.addClient(this);
            sendSuccess(request, newUser);
            sendRoomInfos(null);
            return true;
        } else {
            sendError(request, 400, "Tên tài khoản hoặc mật khẩu "
                    + "không chính xác, vui lòng kiểm tra lại.");
            return false;
        }
    }

    public void handleRegister(Request request) {
        RegisterRequest register = (RegisterRequest) request.getDataObject();
        String username = register.username;
        String password = register.password;
        String fullName = register.fullname;
        String email = register.email;
        if (userDao.insertUser(username, password, fullName, email)) {
            sendSuccess(request);
        } else {
            sendError(request, 500, "Tên tài khoản đã tồn tại,"
                    + " vui lòng chọn tên khác.");
        }
    }

    public void handleGoRoom(Request request) {
        GoRoomRequest data = (GoRoomRequest) request.getDataObject();
        if (room != null) {
            sendError(request, 600, "Bạn đã ở trong phòng nào đó.");
            return;
        }
        if (Room.count >= data.id) {
            Room newRoom = roomPool.goRoom(data.id, this);
            if (newRoom != null) {
                newRoom.sendRoomDetail(user.id);
                sendSuccess(request, newRoom
                        .getRoomDetail());
                sendClientNotInRoom();
                room = newRoom;
            } else {
                sendError(request, 601, "Phòng này đã bị đầy.");
            }
        } else {
            sendError(request, 602, "Phòng này không tồn tại.");
        }
    }

    public void handleChat(Request request) {
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(
                user.id, chatMessageReceive.message);

        Response res = Response.success(
                request, chatMessageSend);
        sendSuccess(request);

        room.sendOneClient(chatMessageReceive.id, res);
    }

    public void handleChatAll(Request request) {
        ChatMessage chatMessageReceive = (ChatMessage) request.getDataObject();
        ChatMessage chatMessageSend = new ChatMessage(
                user.id, chatMessageReceive.message);

        Response res = Response.success(
                request, chatMessageSend);
        sendSuccess(request, chatMessageSend);

        room.sendToAll(res, user.id);
    }

    public void handleKick(Request request) {
        if (room.isHost(user.id)) {
            int clientId = Integer.parseInt(request.data);
            Client kClient = clientPool.getClient(clientId);
            kClient.outRoom();
            kClient.send(Response.success(
                    request, roomPool.getRoomInfos()));
        } else {
            sendError(request, 1100, "Bạn không phải là chủ phòng");
        }
    }

    public void handlePlay(Request request) {
        if (room.isHost(user.id)) {
            if (room.canPlay()) {
                sendSuccess(request);
                room.playGame();
            } else {
                sendError(request, 1000, "Có ai đó chưa sẵn sàng.");
            }
        } else {
            sendError(request, 1001, "Bạn không phải là chủ phòng.");
        }
    }

    public void handleBat(Request request) {
        Bet bet = (Bet) request.getDataObject();
        if (!room.isGameStarted()) {
            sendError(request, 900, "Trò chơi chưa bắt đầu.");
        } else {
            if (room.setBat(user.id, bet)) {
                sendSuccess(request);
            } else {
                sendError(request, 901, "Bạn không đủ tiền để đặt cược.");
            }
        }
    }

    public void handleReady(Request request) {
        if (room.setReady(user.id)) {
            sendSuccess(request);
        } else {
            sendError(request, 800, "Bạn đã sẵn sàng rồi.");
        }
    }

    public void handleUnready(Request request) {
        if (room.setUnready(user.id)) {
            sendSuccess(request);
        } else {
            sendError(request, 1200, "Bạn đang chưa sẵn sàng.");
        }
    }

    public void handleOutRoom(Request request) {
        if (outRoom()) {
            sendSuccess(request, roomPool.getRoomInfos());
        } else {
            sendError(request, 700, "Bạn không ở trong bất kỳ phòng nào");
        }
    }

    public void sendRoomInfos(Request request) {
        Response res = Response.success(
                "list_room", roomPool.getRoomInfos());
        send(res);
    }

    private void sendError(Request request, int code, String msg) {
        Response response = Response.error(code, request, msg);
        send(response);
    }

    private void sendSuccess(Request request, Object data) {
        send(Response.success(request, data));
    }

    private void sendSuccess(Request request) {
        send(Response.success(request));
    }

    private void printLog(String json, boolean req) {
        String remoteIp = socket
                .getRemoteSocketAddress()
                .toString();
        remoteIp = remoteIp.replace("/", "");
        String action = "request";
        if (req == false) {
            action = "response";
        }
        Log.d("IP: " + remoteIp + ", "
                + action, json);
    }

    private synchronized void schedulePing() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                send(Response.ping());
            }
        };
        timer.schedule(task, 60000);
    }

    public boolean outRoom() {
        if (room != null) {
            room.remove(user.id);
            room = null;
            sendClientNotInRoom();
            return true;
        }
        return false;
    }

    public void sendClientNotInRoom() {
        ThreadUtils.runInNewThread(() -> {
            clientPool.clientsNotInRoom().forEach((client) -> {
                if (user.id != client.getId()) {
                    System.out.println("client id: " + client.getId());
                    client.sendRoomInfos(null);
                };
            });
        });
    }

    public boolean notInRoom() {
        return room == null;
    }
    public void sendPersonalInfo(){
        PersonalInfo p = userDao.getPersonalinfo(getId());
        send(new Response("info", p, 200));
    }
    public void handleForgot(Request request) {
        ForgotPasswordRequest forgotPasswordRequest = (ForgotPasswordRequest) request.getDataObject();
        String usernameRequest = forgotPasswordRequest.username;
        String emailRequest = forgotPasswordRequest.email;
        if(!userDao.userExists(usernameRequest)){
            sendError(request, 1100, "Tài khoản không tồn tại, mời bạn nhập lại!");
            return ;
        }
        if(!userDao.getEmail(usernameRequest).equals(emailRequest)){
            sendError(request, 1101, "Email không đúng, mời bạn nhập lại!");
            return ;
        }
        User user = new User();
        user.username = usernameRequest;
        user.email = emailRequest;
        this.user = user;
        String key = forgotUtil.genKey(usernameRequest);
        smtp.SMTP.sendMailToUser(emailRequest, key);
        sendSuccess(request);
    }

    public void handRank(Request request) { // chưa send
        System.out.println("Client get rank");
        List<RankRow> listuser = userDao.getRank();
        System.out.println("Client get rank done");
        sendSuccess(request, listuser);
    }

    void handleSubmitCode(Request request) {
        String key = (String) request.getDataObject();
        System.out.println(key);
        System.out.println(this.user.username);
        int code = forgotUtil.checkKey(this.user.username, key);
        if(code == 200){
            sendSuccess(request);
        }else{
            String message = "";
            if(code == 1200){
                message = "Mã xác nhận đã mất hiệu lực, vui lòng tạo lại mã khác!";
            }else if(code == 1201){
                message = "Mã xác nhận chưa chính xác, vui lòng nhập lại!";
            }else if(code == 1202){
                message = "Bạn chưa sử dụng chức năng quên mật khẩu, vui lòng thử lại!";
            }
            sendError(request, code, message);
        }
    }

    public void handleSubmitNewpass(Request request) {
        String pass = (String) request.getDataObject();
        userDao.setPassword(this.user.username, pass);
        sendSuccess(request);
    }
}
