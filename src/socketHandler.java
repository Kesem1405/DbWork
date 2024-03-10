import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class socketHandler extends Thread {
	Socket incoming;
	Sql sql;

	socketHandler(Socket _in, Sql sql) {
		this.incoming = _in;
		this.sql = sql;
	}

	@Override
	public void run() {
		try (ObjectInputStream inFromClient = new ObjectInputStream(incoming.getInputStream());
			 DataOutputStream outToClient = new DataOutputStream(incoming.getOutputStream())) {
			while (true) {
				try {
					Object obj = inFromClient.readObject();
					if (obj instanceof Student) {
						Student s = (Student) obj;
						Sql.insertStatement(s.getId(), s.getName(), s.getPhone()); // Assuming you have getters in your Student class
						outToClient.writeUTF("Operation was successful.\n");
					}
				} catch (ClassNotFoundException e) {
					outToClient.writeUTF("Operation failed: invalid object type.\n");
					e.printStackTrace();
				} catch (Exception e) {
					outToClient.writeUTF("An error occurred.\n");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				incoming.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
