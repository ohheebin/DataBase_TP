import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {

	String inputtedUserID;//�α����� ȸ�� ID�� �����ϱ� ���� ���� ����
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
	private static final String USER = "HEEBIN";
	private static final String PASS = "heebin";

	private static Connection conn = null;
	private static DatabaseMetaData meta = null;

	Scanner scan;

	public Customer() {
		scan = new Scanner(System.in);
	}

	public boolean createConn() {
		try {
			Class.forName(DRIVER);
			System.out.println("[*] JDBC ����̹� �ε� �Ϸ�.");
			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("[*] �����ͺ��̽� ���� �Ϸ�.");
		} catch (Exception e) {
			System.out.println("[*] �����ͺ��̽� ���� ���� �߻�: \n" + e.getMessage());
			return false;
		}

		return true;
	}

	public Connection getConn() {
		return conn;
	}

	public DatabaseMetaData getDBMD(Connection conn) {
		try {
			meta = conn.getMetaData();
		} catch (Exception e) {
			System.out.println("[*] DBMD ���� �߻�: \n" + e.getMessage());
		}

		return meta;
	}
	//ȸ���� ID�� �Է¹ް� ���̺� �����ϸ� password�� Ȯ���ϰ� ���� Ʃ�� �����ϸ� �α����� �Ѵ�.
	public boolean login(Connection conn) {
		String userPW = "";
		String inputPW;

		System.out.print("What is your ID? : ");
		inputtedUserID = scan.nextLine();
		System.out.print("What is your pw? : ");
		inputPW = scan.nextLine();

		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			userPW = rs.getString(1);
			if (inputPW.equals(userPW)) {
				System.out.println("Log-in success!");
				return true;
			} else {
				System.out.println("Fail to Log-in");
				return false;
			}
		} catch (Exception e) {
			System.out.println("There is not your ID");
			// System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
			return false;
		}

	}
	//���� ȸ������ ID�� �ߺ��� Ȯ���ϰ� ���̺� �������� �ʴ� ��쿡�� ȸ�������� �����Ѵ�.
	public boolean signUp(Connection conn) {
		String inputID = "";
		String inputName, inputPW, inputPhoneNumber, inputAddress, inputBirth;
		boolean nobodyUseThisID = false;
		while (nobodyUseThisID == false) {
			System.out.print("ID : ");
			inputID = scan.nextLine();
			//�� ���̺� ȸ�� ID�� ������ Ȯ���Ѵ�.
			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_ID FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputID + "'");
				rs.next();
				rs.getString(1);
				System.out.println("You cannot use this ID.");
				System.out.println("Please input another ID.");

			} catch (Exception e) {
				System.out.println("You can use this ID.");
				nobodyUseThisID = true;
			}
		}
		System.out.print("Name : ");
		inputName = scan.nextLine();
		System.out.print("PW : ");
		inputPW = scan.nextLine();
		System.out.print("Phone number : ");
		inputPhoneNumber = scan.nextLine();
		System.out.print("Address : ");
		inputAddress = scan.nextLine();
		System.out.print("Birth(e.g. 93/07/12) : ");
		inputBirth = scan.nextLine();

		return this.insert(conn, "INSERT INTO CUSTOMER VALUES('" + inputID + "','" + inputName + "','" + inputPW + "','"
				+ inputPhoneNumber + "','" + inputAddress + "','" + inputBirth + "',0,0)");

	}
	//���Ÿ� �ϴ� �Լ� ���� ���� �������� �����Ѵ�.
	public void bookingMovie(Connection conn) {
		String movieId, movieName, screenNum = "";
		String inputCinemaName, inputMovieName, inputSeatNum;
		int reservationSeatCount = 0, remainSeat, screenTotalSeatNumber, reservationNumber = 0;
		String inputScreeningNumber;
		//��ȭ���� �̸����� ��½��Ѽ� ��ȭ���� �����Ѵ�.
		try {
			ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
			System.out.println("=================================================");
			while (rs.next()) {
				System.out.println("��ȭ�� �̸� : " + rs.getString(1));
			}

		} catch (Exception e) {
			System.out.println("��ȭ���� �������� �ʽ��ϴ�.");
		}
		System.out.println("----------------------------------------------------");
		System.out.print("��ȭ�� ���� : ");
		inputCinemaName = scan.nextLine();
		// ��ȭ���� �����ϸ� �� ��ȭ������ ������ ��ȭ�� �����ش�.
		try {
			ResultSet rs = this.select(conn, "SELECT MOVIE_ID, MOVIE_NAME FROM MOVIE WHERE MOVIE_ID IN ("
					+ "SELECT MOVIE_ID FROM SCREENING WHERE CINEMA_NAME = '" + inputCinemaName + "')");
			System.out.println("----------------------------------------------------");
			while (rs.next()) {
				movieId = rs.getString(1);
				movieName = rs.getString(2);
				System.out.println("MOVIE_ID : " + movieId + "\t MOVIE_NAME : " + movieName);
			}
		} catch (Exception e) {
			System.out.println("�� ��ȭ������ ��ȭ�� ������ �ʽ��ϴ�.");
		}
		System.out.println("----------------------------------------------------");
		// ��ȭ������ ������ ��ȭ�� �����ϸ� ��ȭ ���������� �����ְ� �� ��ȣ�� �����Ѵ�.
		System.out.print("��ȭ ���� : ");
		inputMovieName = scan.nextLine();
		try {
			ResultSet rs = this.select(conn,
					"SELECT SCREENING_TIME, MOVIE_ID, SCREENING_NUMBER, SCREEN_NUMBER FROM SCREENING WHERE CINEMA_NAME = '"+inputCinemaName+"' AND MOVIE_ID = ("
							+ "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_NAME = '" + inputMovieName + "')");
			System.out.println("----------------------------------------------------");
			System.out.println("������ ��ȭ : " + inputMovieName);
			rs.next();
			do {
				screenNum = rs.getString(4);
				System.out.println("�󿵹�ȣ : " + rs.getString(3) + "\t������ : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("������ ��ȭ�� �� �� �ƽ��ϴ�.");
		}
		System.out.println("----------------------------------------------------");

		System.out.print("�󿵹�ȣ ���� : ");
		inputScreeningNumber = scan.nextLine();

		// ������ �ż��� �����Ѵ�.
		try {
			//������ ��ȭ���� �󿵰� ���� �� �󿵰��� ��ü �¼� ���� �޾ƿ´�.
			ResultSet screen = this.select(conn, "SELECT SCREEN_TOTAL_SEAT_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = "
					+ screenNum + " AND CINEMA_NAME = '" + inputCinemaName + "'");
			screen.next();
			screenTotalSeatNumber = Integer.parseInt(screen.getString(1));
			//������ �󿵰��� ���� �¼� ������ �޾ƿ´�.
			ResultSet rs = this.select(conn,
					"SELECT REMAIN_SEAT_NUMBER FROM SCREENING WHERE SCREENING_NUMBER = " + inputScreeningNumber);
			rs.next();
			remainSeat = Integer.parseInt(rs.getString(1));//�󿵰��� ���� �¼��� �����ϴ� ����
			System.out.print("������ �ż� ����(���� �¼� �� : " + remainSeat + ") : ");
			reservationSeatCount = scan.nextInt();
			scan.nextLine();
			int temp = reservationSeatCount;
			// ������ �ż���ŭ �¼��� �����Ѵ�. �̹� ���ŵ� �¼��� ���� ������ ��½�Ű�� ������ ����ŭ �¼��� �����Ѵ�.
			System.out.println("----------------------------------------------------");
			if (reservationSeatCount <= remainSeat) {
				int[] checkRemainSeat = new int[100];//���ŵ� �¼��� ���� ������ Ȯ���ϱ����� ����
				for (int i = 0; i < reservationSeatCount;) {
					System.out.println("�¼�����<1~" + screenTotalSeatNumber + "> ");//��ü �¼����� ���� �ش�.
					try {
						ResultSet rt = this.select(conn,
								"SELECT SEAT_NUMBER FROM TICKET WHERE SCREENING_NUMBER = " + inputScreeningNumber);
						rt.next();
						do {
							checkRemainSeat[Integer.parseInt(rt.getString(1))] = 1;
						} while (rt.next());
						//���ŵ� �¼��� ���� ������ �����ش�.
						System.out.print("<���ŵ� �¼�(");
						for (int j = 0; j < checkRemainSeat.length; j++) {
							if (checkRemainSeat[j] == 1)
								System.out.print(j + " ");
						}
						//�¼� ������ ���� ���� ���� ���� �����ش�.
						System.out.print("), ���� ���� ��(" + (reservationSeatCount - i) + ")> : ");
					} catch (Exception e) {
						System.out.print("<���� ���� ��(" + (reservationSeatCount - i) + ")> : ");
					}
					inputSeatNum = scan.nextLine();//�¼��� ����
					//���� ���̺��� insert�ϰ� Ƽ�� ���̺� �Է��� ������ insert�Ѵ� �׸��� �������� �����ߴٸ� �ٽ� �¼� �������� ���ư���.
					//�� ���̺� �����ϴ� ticketing���� 1���� ��Ű�� �� ���̺��� ���� �¼��� 1 ���� ��Ų��.
					try {
						try {
							if (reservationNumber == 0) {
								insert(conn, "INSERT INTO RESERVATION VALUES (RESERVATENUM.NEXTVAL,"
										+ inputScreeningNumber + ",'" + inputtedUserID + "')");
								ResultSet ru = this.select(conn, "SELECT RESERVATENUM.CURRVAL FROM DUAL");
								ru.next();
								reservationNumber = Integer.parseInt(ru.getString(1));
							}
						} catch (Exception e) {
							System.out.println("�������̺������ ����");
						}
						insert(conn,
								"INSERT INTO TICKET VALUES (TICKETNUM.NEXTVAL," + inputSeatNum + "," + reservationNumber
										+ ",'" + inputCinemaName + "'," + screenNum + "," + inputScreeningNumber + ")");
						update(conn,
								"UPDATE CUSTOMER SET CUSTOMER_TIKETING_NUMBER = CUSTOMER_TIKETING_NUMBER + 1 WHERE CUSTOMER_ID = '"
										+ inputtedUserID + "'");
						update(conn,
								"UPDATE SCREENING SET REMAIN_SEAT_NUMBER = REMAIN_SEAT_NUMBER - 1 WHERE SCREENING_NUMBER = "
										+ inputScreeningNumber);
						i++;
					} catch (Exception e) {
						System.out.println("Ƽ�����̺� ����");
					}
				}
				this.doPayment(conn, reservationSeatCount, reservationNumber);//���� function�� �ҷ��´�.
			} else {
				System.out.println("���� �¼��� �����մϴ�.");
			}
		} catch (Exception e) {
			System.out.println("�� �������� ��ȭ ���� �����ϴ�.");
		}
		System.out.println("=================================================");
	}
	
	//���� ���� ������ ������ ���� ����
	private void doPayment(Connection conn, int numOfTicket, int reservationNum) {
		int inputMethodToPayment = 0;
		int point;
		int usedPoint = 0;//���� ����Ʈ
		int totalCost = 10000 * numOfTicket;//������ �Ѱ��� ������ Ƽ�ϴ� 10000��
		//���� �������� ���ͳ� �������� �����ϰ� ���ͳ� �����̸� ������ ���� ���� �����̸� manager�� ���� ������ �ϰ� �Ѵ�.
		while (inputMethodToPayment != 1 && inputMethodToPayment != 2) {
			System.out.print("�����ϴ� �����? (1: ���� ����, 2: ���ͳ� ����) : ");
			inputMethodToPayment = scan.nextInt();
			scan.nextLine();
			switch (inputMethodToPayment) {
			//���� ������ ��� manager�� ������ ó���Ѵ�.
			case 1:
				break;
			//���ͳ� ����
			case 2:
				try {
					ResultSet rs = this.select(conn,
							"SELECT CUSTOMER_POINT FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					rs.next();
					point = Integer.parseInt(rs.getString(1));
				} catch (Exception e) {
					System.out.println("�� ���̺� ����");
					break;
				}
				System.out.println("���ͳ� ������ �����մϴ�.");
				System.out.println("����� �� ���� �ݾ��� " + totalCost + "�Դϴ�.");
				System.out.println("����� ����Ʈ�� " + point + "p �ֽ��ϴ�.");
				//����Ʈ�� 1000���� ��쿡�� ����Ʈ�� ����ϰ� �Ѵ�.
				if (point >= 1000) {
					int tempInput = 0;
					while (tempInput != 1 && tempInput != 2) {
						System.out.print("����Ʈ�� ����Ͻðڽ��ϱ�? (1:Yse, 2:No) : ");
						tempInput = scan.nextInt();
						scan.nextLine();
						switch (tempInput) {
						case 1:
							boolean tempUseCheck = false;
							while (tempUseCheck == false) {
								System.out.print("���� ����Ʈ�� ����Ͻðڽ��ϱ�? : ");
								usedPoint = scan.nextInt();
								scan.nextLine();
								//�� ���̺� �����ϴ� point�� ����� ����Ʈ�� ���ؼ� �ʰ����� �ʰ� ó���Ѵ�.
								if (usedPoint <= point) {
									tempUseCheck = true;
								} else {
									System.out.println("����� �� �ִ� ����Ʈ�� �ʰ��߽��ϴ�. �ٽ� �Է��ϼ���");
								}
							}
							break;
						case 2:
							break;
						default:
							System.out.println("Wrong input. Please input again.");
							break;
						}
					}
				} else {
					System.out.println("����Ʈ �������� �������� ����Ͻ� �� �����ϴ�. ");
				}
				//�� ���ݰ� ����Ʈ��뷮�� üũ�ؼ� �� �����ؾ��� ������ �����Ѵ�.
				int cash = totalCost - usedPoint;
				//PAYMENT���̺� ������ insert�Ѵ� �������� 0�� ������ �߱� ���θ� Ȯ�����ֱ� ���� ���� �Ӽ��̴�.
				this.insert(conn, "INSERT INTO PAYMENT VALUES( PAYMENTNUM.NEXTVAL," + usedPoint + "," + cash + ",'"
						+ inputtedUserID + "'," + reservationNum + ",0)");
				int addPoint = numOfTicket * 100;//Ƽ������� 100�� point�� �ش�.
				//����Ʈ ������ �� ���̺� update�Ѵ�.
				this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT+" + addPoint
						+ "WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				System.out.println("������ �����߽��ϴ�!");
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}
	//�������� ���� ��ȭ ��Ʈ�� �����ش�.
	public void SearchMovie(Connection conn) {
		try {
			//�������� ���� ��ȭ ������ ���� ������ �����ش�.
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_NAME, RESERVATION_RATE FROM MOVIE ORDER BY RESERVATION_RATE DESC");
			System.out.println("=================================================");
			System.out.printf("%30s		|%10s\n", "��ȭ ����", "������");
			System.out.println("-------------------------------------------------");
			rs.next();
			do {
				System.out.printf("%30s		|%10s%s\n", rs.getString(1), rs.getString(2), "%");
			} while (rs.next());
			System.out.println("=================================================");
			System.out.println();
		} catch (Exception e) {
			System.out.println("There is no movie.");
		}
	}
	//ȸ�� ������ �����ϴ� function
	public void modifyMyInformation(Connection conn) {
		try {
			int inputForModify = 0;

			System.out.println(inputtedUserID);//���� �α��ε� ȸ���� ID
			ResultSet rs = this.select(conn, "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			//���� �α��ε� ȸ���� ������ ��½����ش�.
			System.out.println("======================================================");
			System.out.println("This is your information");
			System.out.println("------------------------------------------------------");
			System.out.println("ID : " + rs.getString(1));
			System.out.println("Name : " + rs.getString(2));
			System.out.println("Password : " + rs.getString(3));
			System.out.println("Phone number : " + rs.getString(4));
			System.out.println("Adderss : " + rs.getString(5));
			System.out.println("Birth : " + rs.getString(6));
			System.out.println("Point : " + rs.getString(7));
			System.out.println("======================================================");
			//�����ϰ��� �ϴ� �κ��� ������ ������ �Ѵ�.
			while (inputForModify != 6) {
				System.out.print("What do you want to change? (1: name, 2: password, 3: phone number,"
						+ " 4: address, 5: birth, 6: exit)");
				inputForModify = scan.nextInt();
				scan.nextLine();
				String userInput = "";
				if (inputForModify != 5 && inputForModify != 6) {
					System.out.print("How do you want to change? : ");
					userInput = scan.nextLine();
				}
				switch (inputForModify) {
				case 1:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_NAME = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 2:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_PW = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 3:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_PHONENUMBER = '" + userInput
							+ "' WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					break;
				case 4:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_ADDRESS = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 5:
					System.out.print("How do you want to change?(e.g. 93/07/12) : ");
					userInput = scan.nextLine();
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_BRITH = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 6:
					break;
				default:
					System.out.println("Wrong input!!!");
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Wrong ID.");
		}
	}
	//���� �α��ε� ȸ���� ���� ���� ����, ���� ������ �ϴ� function
	public void checkMyReservation(Connection conn) {
		int indexForReservation = 0;
		try {
			//�α��ε� ȸ���� ������ �����ȣ, ��ȭ����, ��ȭ��, �󿵽ð�, �󿵰��� �����ش�.
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_ID, RESERVATION_NUMBER, CINEMA_NAME, SCREENING_TIME, SCREEN_NUMBER FROM RESERVATION, SCREENING WHERE RESERVATION.CUSTOMER_ID = '"
							+ inputtedUserID + "' AND SCREENING.SCREENING_NUMBER = RESERVATION.SCREENING_NUMBER");
			System.out.println(
					"==================================================================================================");
			System.out.printf("%10s      %20s       %10s        %15s        %5s\n", "�����ȣ", "��ȭ ����", "��ȭ��", "�� �ð�",
					"�󿵰�");
			System.out.println(
					"--------------------------------------------------------------------------------------------------");
			rs.next();
			do {
				ResultSet rt = this.select(conn,
						"SELECT MOVIE_NAME FROM MOVIE WHERE MOVIE_ID = '" + rs.getString(1) + "'");
				rt.next();
				System.out.printf("%10s      %20s       %10s        %15s        %5s\n", rs.getString(2),
						rt.getString(1), rs.getString(3), rs.getString(4), rs.getString(5));
			}while (rs.next());
			System.out.println(
					"==================================================================================================");
			System.out.println();
		} catch (Exception e) {
			System.out.println("There is no reservation.");
		}
		//���ſ� ���ؼ� ������ Ƽ���� �����ߴ����� ���� ��Ҹ� �����Ѵ�.
		while (indexForReservation != 3) {
			System.out.println(
					"What do you want to do? (1: show tickets of reservation, 2: delete reservation, 3: exit) : ");
			indexForReservation = scan.nextInt();
			scan.nextLine();
			String inputReservationNumber;
			switch (indexForReservation) {
			//�ѹ��� ���ſ��� ������ Ƽ���� ����� �����ش�.
			case 1:
				System.out.println("What reservation do you want to see more? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				try {
					ResultSet rs = this.select(conn,
							"SELECT * FROM TICKET WHERE RESERVATION_NUMBER = " + inputReservationNumber);
					System.out.println(
							"==================================================================================================");
					System.out.printf("%10s      %10s       %10S        %10S        %5S\n", "Ƽ�Ϲ�ȣ", "�¼���ȣ", "�����ȣ",
							"��ȭ��", "�󿵰�");
					System.out.println(
							"--------------------------------------------------------------------------------------------------");
					while (rs.next()) {
						System.out.printf("%10s      %10s       %10S        %10S        %5S\n", rs.getString(1),
								rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
					}
					System.out.println(
							"==================================================================================================");
					System.out.println();
				} catch (Exception e) {
					System.out.println("You input wrong number");
				}
				break;
			//���� ���
			case 2:
				String screeningNumber = "";
				System.out.println("What reservation do you want to delete? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				//����� ���� ��ȣ�� �����ϰ� ������ ���Ź�ȣ�� ��ġ�ϴ� ���� ���̺�� Ƽ�� ���̺��� ������ �����Ѵ�.
				//���� ��Ҹ� �ϸ� point�� Ƽ�ϼ�*100��ŭ ���� ��Ű�� ���� Ƽ���ü��� ���� ��Ų��.
				try {
					ResultSet rs = this.select(conn, "SELECT SCREENING_NUMBER FROM TICKET WHERE RESERVATION_NUMBER = "
							+ Integer.parseInt(inputReservationNumber));
					int ticketCount = 0;
					while (rs.next()) {
						ticketCount++;
						screeningNumber = rs.getString(1);
					}
					delete(conn, "DELETE FROM RESERVATION WHERE RESERVATION_NUMBER = "
							+ Integer.parseInt(inputReservationNumber));
					update(conn, "UPDATE SCREENING SET REMAIN_SEAT_NUMBER = REMAIN_SEAT_NUMBER +" + ticketCount
							+ " WHERE SCREENING_NUMBER = '" + screeningNumber + "'");
					update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT -" + (ticketCount * 100)
							+ " WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					update(conn, "UPDATE CUSTOMER SET CUSTOMER_TIKETING_NUMBER = CUSTOMER_TIKETING_NUMBER -" + ticketCount
							+ " WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				} catch (Exception e) {
					System.out.println("You input wrong number");
				}
				break;
			case 3:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}
	//ȸ�� Ż�� function
	public void secession(Connection conn) {
		int inputForsecession;
		System.out.print("Are you sure to remove your account? (1: Yes, 2: No!) : ");
		inputForsecession = scan.nextInt();
		scan.nextLine();
		switch (inputForsecession) {
		//���� �α��ε� ���� ���� Ʃ���� �� ���̺��� ���� ��Ų��.
		case 1:
			this.delete(conn, "DELETE FROM Customer WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			break;
		case 2:
			break;
		default:
			System.out.println("Wrong input!!!");
			break;
		}
	}

	public boolean insert(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			int rowCount = stmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("������ ���� ����");
				return false;
			} else {
				System.out.println("������ ���� ����");
			}
		} catch (Exception e) {
			System.out.println("[*] INSERT ���� �߻�: \n" + e.getMessage());
		}

		return true;
	}

	public ResultSet select(Connection conn, String query) {
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsMeta = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			rsMeta = rs.getMetaData();

			// ���� ��� ��Ÿ ���� ����
			for (int col = 1; col <= rsMeta.getColumnCount(); col++) {
				int type = rsMeta.getColumnType(col);
				String typeName = rsMeta.getColumnTypeName(col);
				String name = rsMeta.getColumnName(col);
				// System.out.println(col + "st column " + name + " is JDBC type
				// " + type + " which is called " + typeName);
			}

			// ���� ��� ��ȯ
			return rs;
		} catch (Exception e) {
			System.out.println("[*] SELECT ���� �߻�: \n" + e.getMessage());
		}

		return rs;
	}

	public boolean update(Connection conn, String query) {
		try {
			Statement pstmt = conn.createStatement();
			int rowCount = pstmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("������ ���� ����");
			} else {
				System.out.println("������ ���� ����");
			}
		} catch (Exception e) {
			System.out.println("[*] UPDATE ���� �߻�: \n" + e.getMessage());
		}

		return true;
	}

	public boolean delete(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			int rowCount = stmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("������ ���� ����");
				return false;
			} else {
				System.out.println("������ ���� ����");
			}
		} catch (Exception e) {
			System.out.println("[*] DELETE ���� �߻�: \n" + e.getMessage());
		}

		return true;
	}
}