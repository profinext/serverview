import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerMonitor {

    static class Server {
        String ipAddress;
        int port;
        boolean isUp;

        public Server(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.isUp = false;
        }

        // Проверяем доступность сервера с помощью ping
        public void checkStatus() {
            try {
                Process process = Runtime.getRuntime().exec("ping -c 1 " + ipAddress);
                int exitCode = process.waitFor();
                isUp = (exitCode == 0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                isUp = false;
            }
        }

        public boolean isUp() {
            return isUp;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getPort() {
            return port;
        }
    }

    // Список серверов для мониторинга
    private List<Server> servers = new ArrayList<>();
    private Timer timer = new Timer();

    public void addServer(String ipAddress, int port) {
        servers.add(new Server(ipAddress, port));
    }

    // Метод для автоматической проверки серверов каждую минуту
    public void startMonitoring() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Server server : servers) {
                    server.checkStatus();
                    if (!server.isUp()) {
                        sendAlert(server);
                    }
                }
            }
        }, 0, 60000); // Каждые 60 секунд
    }

    // Метод для отправки уведомления при падении сервера
    private void sendAlert(Server server) {
        System.out.println("ALERT: Server with IP " + server.getIpAddress() + " on port " + server.getPort() + " is DOWN!");
        // Здесь можно интегрировать API для отправки уведомлений (например, email, Slack, SMS и т.д.)
    }

    public static void main(String[] args) {
        ServerMonitor monitor = new ServerMonitor();
        monitor.addServer("192.168.0.101", 80);
        monitor.addServer("192.168.0.102", 443);
        monitor.startMonitoring();
    }
}
