import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Manager {

	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
	private static final String USER = "HEEBIN";
	private static final String PASS = "heebin";

	private static Connection conn = null;
	private static DatabaseMetaData meta = null;

	Scanner scan;

	public Manager() {
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

	// ��ȭ ���, ����, ������ �ϴ� �Լ�
	public void manageMovie(Connection conn) {
		int inputForManageMovie = 0;
		String movieId;
		String name, id;
		String MovieId, MovieName, information, director, grade, MovieCast;
		int reservationRate, runingtime;

		while (inputForManageMovie != 4) {
			System.out.print("What do you want to do? (1: ��ȭ ���, 2: ��ȭ ����, 3: ��ȭ ���� ����, 4: exit) : ");
			inputForManageMovie = scan.nextInt();
			scan.nextLine();

			switch (inputForManageMovie) {
			// ��ȭ��� ��ȭID, ��ȭ����, ����Ÿ��, ��ȭ����, ���, ����, ��츦 �Է��ؼ� ���̺� �����Ų��.
			case 1:
				System.out.println("=================================================");
				System.out.print("��ȭID? : ");
				MovieId = scan.nextLine();
				System.out.print("��ȭ����? : ");
				MovieName = scan.nextLine();
				System.out.print("RuningTime? : ");
				runingtime = scan.nextInt();
				scan.nextLine();
				System.out.print("��ȭ����? : ");
				information = scan.nextLine();
				System.out.print("GRADE?(��ü, 12, 15, 19) : ");
				grade = scan.nextLine();
				System.out.print("����? : ");
				director = scan.nextLine();
				System.out.print("���? : ");
				MovieCast = scan.nextLine();
				System.out.println("=================================================");
				insert(conn, "INSERT INTO MOVIE VALUES('" + MovieId + "','" + MovieName + "'," + 0 + "," + runingtime
						+ ",'" + information + "','" + grade + "','" + director + "','" + MovieCast + "')");
				break;
			// ��ϵ� ��ȭ ���� ��ȭID�� �Է��ؼ� ��ϵ� ��ȭ������ �����Ѵ�.
			case 2:
				System.out.println("=================================================");
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_NAME, MOVIE_ID FROM MOVIE");
					rs.next();
					do {
						name = rs.getString(1);
						id = rs.getString(2);
						System.out.println("ID : " + id + " MOVIE_NAME : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("��ϵ� ��ȭ�� �����ϴ�. ");
				}
				System.out.println("=================================================");
				System.out.print("������ ��ȭID? : ");
				movieId = scan.nextLine();
				// ������ ��ȭID�� �Է��ؼ� ���̺� �����ϸ� DELETE �������� ���� ��쿡�� ������ ��½�Ų��.
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_ID = '" + movieId + "'");
					rs.next();
					id = rs.getString(1);
					if (movieId.equals(id)) {
						delete(conn, "DELETE FROM MOVIE WHERE MOVIE_ID = '" + movieId + "'");
						System.out.println("delete success!");
					} else {
						System.out.println("Fail to delete");
					}
				} catch (Exception e) {
					System.out.println("������ ��ȭ�� �����ϴ�.");
				}
				break;
			// ��ȭ������ ���ؼ� �����Ѵ�. ������ ��ȭ ID�� �����ؼ� ��ȭ ���̺� �����ϴ� ��ȭ ID�� �����ϰ� ������ ��������
			// ������ �� �ִ�.
			case 3:
				int inputForModifyMovieInformation = 0;
				System.out.println("=================================================");
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_NAME, MOVIE_ID FROM MOVIE");
					rs.next();
					do {
						name = rs.getString(1);
						id = rs.getString(2);
						System.out.println("ID : " + id + " MOVIE_NAME : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
				}
				System.out.println("=================================================");
				System.out.print("������ ��ȭID? : ");
				movieId = scan.nextLine();
				// �Է��� ��ȭID�� ���� ��ȭID�� ���̺� ������ ��� ��ȭ ������ �����ϰ� ���ٸ� ������ ��½�Ų��.
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_ID = '" + movieId + "'");
					rs.next();
					id = rs.getString(1);
					// ��ȭ ���� ���� ����
					if (movieId.equals(id)) {
						while (inputForModifyMovieInformation != 8) {
							System.out
									.print("������ �����Ͻðڽ��ϱ�? (1: MOVIE_NAME, 2: RESERVATION_RATE, 3: RUNINGTIME, 4: INFORMATION"
											+ ", 5: GRADE, 6: DIRECTOR, 7: MOVIE_CAST, 8: exit) : ");
							inputForModifyMovieInformation = scan.nextInt();
							scan.nextLine();
							switch (inputForModifyMovieInformation) {
							case 1:
								System.out.print("������ MOVIE_NAME? : ");
								MovieName = scan.nextLine();
								update(conn, "UPDATE MOVIE SET MOVIE_NAME = '" + MovieName + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 2:
								System.out.print("������ RESERVATION_RATE?(100����) : ");
								reservationRate = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE MOVIE SET RESERVATION_RATE = " + reservationRate
										+ " WHERE MOVIE_ID = '" + movieId + "'");
								break;
							case 3:
								System.out.print("������ RUNINGTIME? : ");
								runingtime = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE MOVIE SET RUNINGTIME = " + runingtime + " WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 4:
								System.out.print("������ INFORMATION? : ");
								information = scan.nextLine();
								update(conn, "UPDATE MOVIE SET INFORMATION = '" + information + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 5:
								System.out.print("������ GRADE?(��ü, 12, 15, 19) : ");
								grade = scan.nextLine();
								update(conn,
										"UPDATE MOVIE SET GRADE = '" + grade + "' WHERE MOVIE_ID = '" + movieId + "'");
								break;
							case 6:
								System.out.print("������ DIRECTOR? : ");
								director = scan.nextLine();
								update(conn, "UPDATE MOVIE SET DIRECTOR = '" + director + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 7:
								System.out.print("������ MOVIE_CAST? : ");
								MovieCast = scan.nextLine();
								update(conn, "UPDATE MOVIE SET MOVIE_CAST = '" + MovieCast + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 8:
								break;
							default:
								System.out.println("Wrong input!!!");
								break;
							}
						}
					}
				} catch (Exception e) {// �Է��� ��ȭID�� DB�� �������� �ʴ°��
					System.out.println("�Է��� ��ȭID�� �������� �ʽ��ϴ�.");
				}
				break;
			case 4:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}

	}

	// ��ȭ�� ���� ���, ����, ����
	public void manageCinema(Connection conn) {
		int inputForManageCinema = 0;
		String name, cinemaName, cinemaAddress, cinemaPhonenumber;
		int cinemaTotalSeatNumber, cinemaScreenNumber;
		while (inputForManageCinema != 4) {
			System.out.print(
					"What do you want to do? (1: Upload cinema, 2: delete cinema, 3: modify cinema information, 4: exit) : ");
			inputForManageCinema = scan.nextInt();
			scan.nextLine();
			System.out.println("=================================================");
			switch (inputForManageCinema) {
			case 1:// ��ȭ�����
				System.out.print("��ȭ���̸�? : ");
				cinemaName = scan.nextLine();
				System.out.print("��ȭ���ּ�? : ");
				cinemaAddress = scan.nextLine();
				System.out.print("��ȭ����ȭ��ȣ? : ");
				cinemaPhonenumber = scan.nextLine();
				System.out.print("��ȭ�����¼���? : ");
				cinemaTotalSeatNumber = scan.nextInt();
				scan.nextLine();
				System.out.print("��ȭ�� ��ü �󿵰���? : ");
				cinemaScreenNumber = scan.nextInt();
				scan.nextLine();

				this.insert(conn, "INSERT INTO CINEMA VALUES('" + cinemaName + "','" + cinemaAddress + "','"
						+ cinemaPhonenumber + "'," + cinemaTotalSeatNumber + "," + cinemaScreenNumber + ")");
				System.out.println("=================================================");
				break;

			case 2:// ��ȭ������

				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					rs.next();
					do {
						name = rs.getString(1);
						System.out.println("CinemaName : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
				}

				System.out.print("������ ��ȭ���̸�? : ");
				cinemaName = scan.nextLine();

				try {
					ResultSet rs = this.select(conn,
							"SELECT CINEMA_NAME FROM CINEMA WHERE CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					name = rs.getString(1);
					if (cinemaName.equals(name)) {
						delete(conn, "DELETE FROM CINEMA WHERE CINEMA_NAME = '" + cinemaName + "'");
						System.out.println("delete success!");
					} else {
						System.out.println("Fail to delete");
					}
				} catch (Exception e) {
					System.out.println("������ ��ȭ���� �������� �ʽ��ϴ�.");
				}
				break;

			case 3:// ��ȭ������
				int inputForModifyCinemaInformation = 0;
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					rs.next();
					do {
						name = rs.getString(1);
						System.out.println("CINEMA_NAME : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
				}

				System.out.print("������ ��ȭ���̸�? : ");
				cinemaName = scan.nextLine();
				// ������ ��ȭ���� �����ϰ� ������ ������ ��½�Ų��.
				try {
					ResultSet rs = this.select(conn,
							"SELECT CINEMA_NAME FROM CINEMA WHERE CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					name = rs.getString(1);
					if (cinemaName.equals(name)) {
						// ������ ���� �����Ѵ�.
						while (inputForModifyCinemaInformation != 5) {
							System.out
									.print("������ �����Ͻðڽ��ϱ�? (1: CINEMA_ADDRESS, 2: CINEMA_PHONENUMBER , 3: CINEMA_TOTAL_SEAT_NUMBER "
											+ "4: TOTAL_SCREEN_NUMBER, 5: exit) : ");
							inputForModifyCinemaInformation = scan.nextInt();
							scan.nextLine();
							switch (inputForModifyCinemaInformation) {
							case 1:
								System.out.print("������ CINEMA_ADDRESS? : ");
								cinemaAddress = scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_ADDRESS = '" + cinemaAddress
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 2:
								System.out.print("������ CINEMA_PHONENUMBER? : ");
								cinemaPhonenumber = scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_PHONENUMBER = '" + cinemaPhonenumber
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 3:
								System.out.print("������ CINEMA_TOTAL_SEAT_NUMBER? : ");
								cinemaTotalSeatNumber = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_TOTAL_SEAT_NUMBER = '" + cinemaTotalSeatNumber
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 4:
								System.out.print("������ TOTAL_SCREEN_NUMBER? : ");
								cinemaScreenNumber = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE CINEMA SET TOTAL_SCREEN_NUMBER = '" + cinemaScreenNumber
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 5:
								break;
							default:
								System.out.println("Wrong input!!!");
								break;
							}
						}
					}
				} catch (Exception e) {
					System.out.println("������ ��ȭ���� �������� �ʽ��ϴ�.");
				}
				break;
			case 4:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}

	// �󿵰��� �����ϸ� ������ �����ʰ� ������ ���� ���Ѵ�.
	public void manageScreen(Connection conn) {
		int inputForManageScreen = 0;
		String cinemaName;
		int screenNumber, screenTotalSeatNumber;
		int totalScreenNumberOfEachCinema = 0, totalSeatNumberOfEachCinema = 0, seatNum = 0, sumSeatNum = 0;
		int num = 0, screenNum, tempScreen;
		while (inputForManageScreen != 2) {
			System.out.print("What do you want to do? (1: Upload screen, 2: exit) : ");
			inputForManageScreen = scan.nextInt();
			scan.nextLine();
			switch (inputForManageScreen) {
			case 1:// �󿵰����
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("CinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
				}
				System.out.println("=================================================");
				System.out.print("�󿵰��� �߰��� ��ȭ�� ���� : ");
				cinemaName = scan.nextLine();
				// �󿵰��� �߰��� ��ȭ���� ���ٸ� ������ ����Ѵ�.
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREEN_TOTAL_SEAT_NUMBER FROM SCREEN WHERE CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					do {
						seatNum = Integer.parseInt(rs.getString(1));
						sumSeatNum = sumSeatNum + seatNum;
						num++;
					} while (rs.next());
				} catch (Exception e) {
				}
				try {
					ResultSet rs = this.select(conn,
							"SELECT CINEMA_TOTAL_SEAT_NUMBER, TOTAL_SCREEN_NUMBER FROM CINEMA WHERE CINEMA_NAME = '"
									+ cinemaName + "'");
					System.out.println("=================================================");
					rs.next();
					do {
						totalSeatNumberOfEachCinema = Integer.parseInt(rs.getString(1));
						totalScreenNumberOfEachCinema = Integer.parseInt(rs.getString(2));
						// ��ȭ���� ��ü �¼����� �󿵰��� �߰��Ҷ����� ���� ��Ų��.
						totalSeatNumberOfEachCinema = totalSeatNumberOfEachCinema - sumSeatNum;
						System.out.println("Total Screen Number : " + totalScreenNumberOfEachCinema
								+ "\nLeft Total Seat Number : " + totalSeatNumberOfEachCinema);
					} while (rs.next());
					System.out.println("=================================================");
				} catch (Exception e) {
					System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
				}
				tempScreen = totalScreenNumberOfEachCinema - num;// ��ȭ���� ��ü
																	// �󿵰����� ����
																	// �󿵰��� �����
																	// �����ֱ� ����
																	// ���� ����
				int i = 0;
				int[] madeScreen = new int[totalScreenNumberOfEachCinema + 1];// �������
																				// �󿵰���
																				// �����ϱ�
																				// ����
																				// ����
																				// ����,
																				// ��������ִٸ�
																				// 1��
																				// �����Ѵ�.
				while (i < tempScreen) {
					do {
						System.out.print("<������� �󿵰� : ");
						try {
							ResultSet rs = this.select(conn,
									"SELECT SCREEN_NUMBER FROM SCREEN WHERE CINEMA_NAME = '" + cinemaName + "'");
							rs.next();
							do {
								screenNum = Integer.parseInt(rs.getString(1));
								System.out.print(screenNum + " ");
							} while (rs.next());
						} catch (Exception e) {
						}
						System.out.println(">");
						System.out.print("�󿵰� ��ȣ(Number) : ");
						screenNumber = scan.nextInt();
						scan.nextLine();
						if (screenNumber > totalScreenNumberOfEachCinema) {// ��ȭ����
																			// ��ü
																			// �¼�����
																			// �ʰ�����
																			// �ʰԲ�
																			// �ϱ�
																			// ����
																			// ����
																			// if��
							System.out.println("���� �󿵰��Դϴ�. �ٽ��Է��ϼ���.");
						} else if (madeScreen[screenNumber] != 0) {
							System.out.println("�̹� ������� �ִ� �󿵰��Դϴ�. �ٽ� �Է� �ϼ���.");
						} else {
							do {
								System.out.print("���ϴ� �¼� �� <���� ��ü �¼���(" + totalSeatNumberOfEachCinema + ")> : ");
								screenTotalSeatNumber = scan.nextInt();
								scan.nextLine();
								if (screenTotalSeatNumber > totalSeatNumberOfEachCinema) {
									System.out.println("�¼��� �ʰ� �Ǿ����ϴ�. �ٽ� �Է��ϼ���");
								}
							} while (screenTotalSeatNumber > totalSeatNumberOfEachCinema);
							totalSeatNumberOfEachCinema = totalSeatNumberOfEachCinema - screenTotalSeatNumber;
							i++;
							madeScreen[screenNumber] = 1;
							insert(conn, "INSERT INTO SCREEN VALUES(" + screenNumber + "," + screenTotalSeatNumber
									+ ",'" + cinemaName + "')");
							for (int j = 1; j <= screenTotalSeatNumber; j++) {
								insert(conn, "INSERT INTO SEAT VALUES('" + j + "','" + cinemaName + "'," + screenNumber
										+ ")");
							}
						}
					} while (screenNumber > totalScreenNumberOfEachCinema);

				}
				break;

			case 2:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}

	// ���� ��ϰ� ������ �ִ�.
	public void manageScreening(Connection conn) {
		int inputForManageScreening = 0, screenTotalSeat = 0;
		String cinemaName, movieId, screeningTime, movieName;
		String screenNumber, screeningNumber;

		while (inputForManageScreening != 3) {
			System.out.print("What do you want to do? (1: �� ���� ���, 2: �� ���� ����, 3: exit) : ");
			inputForManageScreening = scan.nextInt();
			scan.nextLine();
			switch (inputForManageScreening) {
			case 1:// �󿵵��
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("cinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("���� ��ȭ���� �����ϴ�.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("���� �߰��� ��ȭ�� ���� : ");
				cinemaName = scan.nextLine();
				System.out.println("=================================================");
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREEN_NUMBER FROM SCREEN WHERE CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					do {
						screenNumber = rs.getString(1);
						System.out.println("screenNumber : " + screenNumber);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("�� ��ȭ������ ���� �󿵰��� �����ϴ�.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("���� �󿵰� ���� : ");
				screenNumber = scan.nextLine();
				// ������ �󿵰��� ���̺� �������� �ʴ� ��� ���� �߻�
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREEN_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = " + screenNumber);
					rs.next();
					rs.getString(1);
				} catch (Exception e) {
					System.out.println("���� �󿵰��� �߸� �����߽��ϴ�.");
					break;
				}
				// �����̺� �����ϴ� �� ��ȭ���� ������ �󿵰��� �������� �����ش�.
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREENING_TIME, MOVIE_ID FROM SCREENING WHERE SCREEN_NUMBER = " + screenNumber
									+ " AND CINEMA_NAME = '" + cinemaName + "'");
					System.out.println("=================================================");
					rs.next();
					do {
						screeningTime = rs.getString(1);
						movieId = rs.getString(2);
						ResultSet rt = this.select(conn,
								"SELECT MOVIE_NAME FROM MOVIE WHERE MOVIE_ID = '" + movieId + "'");
						rt.next();
						movieName = rt.getString(1);
						System.out.println("movieName : " + movieName + "\t\tscreeningTime : " + screeningTime);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("�ش� �󿵰����� �������� �����ϴ�.");
				}
				System.out.println("=================================================");
				System.out.print("�� ���� �Է� (e.g 2013/12/12 15:20:00) : ");
				screeningTime = scan.nextLine();
				// ���� �� ��ȭ ����
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_NAME, MOVIE_ID FROM MOVIE");
					System.out.println("=================================================");
					rs.next();
					do {
						movieName = rs.getString(1);
						movieId = rs.getString(2);
						System.out.println("movieName : " + movieName + "\tmovieID : " + movieId);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("���� ��ȭ�� �����ϴ�.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("���� ��ȭID ���� : ");
				movieId = scan.nextLine();
				// �󿵰��� ������ �޾ƿͼ� �� ���̺� ������ insert�Ѵ�.
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREEN_TOTAL_SEAT_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = " + screenNumber
									+ " AND CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					screenTotalSeat = Integer.parseInt(rs.getString(1));
				} catch (Exception e) {

				}
				insert(conn,
						"INSERT INTO SCREENING VALUES(SNUM.NEXTVAL,TO_DATE('" + screeningTime
								+ "','YYYY/MM/DD HH24:MI:SS'),'" + movieId + "','" + cinemaName + "'," + screenNumber
								+ "," + screenTotalSeat + ")");
				break;

			case 2:// ����������
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("cinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("�������� ������ ��ȭ���� �����ϴ�.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("�� ������ ������ ��ȭ�� ���� : ");
				cinemaName = scan.nextLine();
				System.out.println("=================================================");
				// ������ ��ȭ���� �������� ���ٸ� ������ ��½�Ų��.
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREENING_NUMBER, SCREENING_TIME, SCREEN_NUMBER FROM SCREENING WHERE CINEMA_NAME = '"
									+ cinemaName + "'");
					rs.next();
					do {
						screeningNumber = rs.getString(1);
						screeningTime = rs.getString(2);
						screenNumber = rs.getString(3);
						System.out.println("screeningNumber : " + screeningNumber + "\tscreenNumber : " + screenNumber
								+ "��" + "\tscreeningTime : " + screeningTime);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("�� ��ȭ������ ���ϴ� �󿵰��� �����ϴ�.");
					System.out.println("=================================================");
					break;
				}
				System.out.println("=================================================");
				System.out.print("�� ������ ������ �� ��ȣ ����  : ");
				screeningNumber = scan.nextLine();
				// ������ �󿵹�ȣ�� �Է� �޾Ƽ� �� ������ ���� ��Ų��.
				try {
					delete(conn, "DELETE FROM SCREENING WHERE SCREENING_NUMBER = '" + screeningNumber + "'");
					System.out.println("delete success!");
				} catch (Exception e) {
					System.out.println("������ �� ��ȣ�� �߸� �Է��߽��ϴ�.");
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

	// vip���� ���� ������ Ƽ�ϼ��� ���� �������� 10����� ��½�Ų��.
	public void manageVIP(Connection conn) {
		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_ID, CUSTOMER_TIKETING_NUMBER FROM CUSTOMER ORDER BY CUSTOMER_TIKETING_NUMBER DESC");
			System.out.println("=================================================");
			System.out.printf("%30s      |%10s\n", "�� ID", "Ƽ�Ͽ���Ƚ��");
			System.out.println("-------------------------------------------------");
			rs.next();
			int i = 1;//10����� ����ϱ����� ���� ����
			do {
				System.out.printf("%30s      |%10s\n", rs.getString(1), rs.getString(2));
				i++;
			} while (rs.next() && i < 11);
			System.out.println("=================================================");
			System.out.println();
		} catch (Exception e) {
			System.out.println("There is no customer.");
		}
	}

	// �߱� ������ �Ϸ��� ȸ���� ������ ���� ���̺� ����Ǳ⿡ ���� ���̺� ����� �͵鸸 �߱��� �� �� �ְ� �Ѵ�.
	public void ticketing(Connection conn) {
		String inputtedUserID = "";
		int paymentNum = 0;
		System.out.println("=================================================");
		boolean checkUserID = false;
		// �� ID ��� �� �߱��� �� �� ID ����
		try {
			ResultSet rs = this.select(conn, "SELECT CUSTOMER_ID FROM CUSTOMER");
			rs.next();
			do {
				System.out.println("�� ID : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("ȸ���� �������� �ʽ��ϴ�.");
		}
		while (checkUserID != true) {
			System.out.println("=================================================");
			System.out.print("�߱��� ���ϴ� ���� ID �Է��ϼ���. : ");
			inputtedUserID = scan.nextLine();
			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				rs.next();
				rs.getString(1);
				checkUserID = true;
			} catch (Exception e) {
				System.out.println("���� ID�Դϴ�. �ٽ� �Է��ϼ���. : ");
			}
		}
		// ����(PAYMENT)���̺� �����ϴ� ���� ��ȣ �� �߱� ������ Ȯ���ϴ� TICKETING�� 0�� ��� �鸸 ��½��Ѽ�
		// �߱��� �ϰ� �Ѵ�.
		try {
			ResultSet rs = this.select(conn,
					"SELECT * FROM PAYMENT WHERE CUSTOMER_ID = '" + inputtedUserID + "' AND TICKETING = 0");
			rs.next();
			do {
				System.out.println("���� ��ȣ : " + rs.getString(1) + ", ���� ��ȣ : " + rs.getString(5));
			} while (rs.next());
			System.out.print("�߱��� ���ϴ� ���� ��ȣ�� �Է��ϼ��� : ");
			paymentNum = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			System.out.println("���������� �����ϴ�. ");
			return;
		}
		// �߱��� �Ϸ�Ǹ� TICKETING�� 1�� update�ϰ� �߱��� �Ϸ�ȴ�.
		update(conn, "UPDATE PAYMENT SET TICKETING = 1 WHERE PAYMENT_NUMBER = " + paymentNum);
		System.out.println("������ȣ " + paymentNum + "�� �߱��� �Ϸ� �Ǿ����ϴ�.");
	}

	// ���� ���� ������ ������ ���� ���ؼ� ������ ���ش�.
	public void helpPayment(Connection conn) {
		String inputtedUserID = "";
		int reservationNum = 0, point = 0, totalCost = 0, usedPoint = 0, ticketCount = 0;
		boolean checkUserID = false;
		// ������ ID�� ��½�Ű�� ���� ������ ������ ���� ����
		try {
			System.out.println("=================================================");
			ResultSet rs = this.select(conn, "SELECT CUSTOMER_ID FROM CUSTOMER");
			rs.next();
			do {
				System.out.println("�� ID : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("ȸ���� �������� �ʽ��ϴ�.");
		}
		while (checkUserID != true) {
			System.out.println("=================================================");
			System.out.print("������ ���ϴ� ���� ID �Է��ϼ���. : ");
			inputtedUserID = scan.nextLine();
			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				rs.next();
				rs.getString(1);
				checkUserID = true;
			} catch (Exception e) {
				System.out.println("���� ID�Դϴ�. �ٽ� �Է��ϼ���. : ");
			}
		}
		// ���� ���̺� �������� �ʴ� ���Ŵ� ���� ������ ������ ���̱⿡ ���� ���̺� �������� �ʰ� ���� ���̺� �����ϴ� ����
		// ��ȣ�� ��½�Ų��.
		try {
			ResultSet rs = this.select(conn, "SELECT RESERVATION_NUMBER FROM RESERVATION WHERE CUSTOMER_ID = '"
					+ inputtedUserID + "' AND RESERVATION_NUMBER NOT IN (SELECT RESERVATION_NUMBER FROM PAYMENT)");
			rs.next();
			do {
				System.out.println("���� ��ȣ : " + rs.getString(1));
			} while (rs.next());
			System.out.print("������ ���ϴ� ���� ��ȣ�� �Է��ϼ��� : ");
			reservationNum = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			System.out.println("���೻���� �����ϴ�. ");
			return;
		}
		// �� ���ſ� �����ϴ� Ƽ���� ���� Ȯ���ϰ� �Ѱ����� �����Ѵ�.
		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_POINT FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			point = Integer.parseInt(rs.getString(1));
			ResultSet rt = this.select(conn, "SELECT * FROM TICKET WHERE RESERVATION_NUMBER = " + reservationNum);
			while (rt.next()) {
				ticketCount++;
			}
			totalCost = ticketCount * 10000;// �� ���� ������ ��Ÿ���� ���� ����
		} catch (Exception e) {
			System.out.println("�� ���̺� ����");
			return;
		}
		System.out.println("���� ������ �����մϴ�.");
		System.out.println("����� �� ���� �ݾ��� " + totalCost + "�Դϴ�.");
		System.out.println("����� ����Ʈ�� " + point + "p �ֽ��ϴ�.");
		// ���� �� ���̺� �����ϴ� point�� 1000�� �̻��� ��츸 point�� ����ϰ� �Ѵ�.
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
		// ����� point��ŭ�� point�� ���̰� ������ ��ŭ�� point�� ������Ű�� ticketing(�߱�����)�� 0����
		// PAYMENT���̺� insert�Ѵ�.
		// �׸��� ���� ���� point�� �� ���̺� update���ش�.
		int cash = totalCost - usedPoint;
		this.insert(conn, "INSERT INTO PAYMENT VALUES( PAYMENTNUM.NEXTVAL," + usedPoint + "," + cash + ",'"
				+ inputtedUserID + "'," + reservationNum + ",0)");
		int addPoint = ticketCount * 100;
		this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT+" + addPoint + " WHERE CUSTOMER_ID = '"
				+ inputtedUserID + "'");
		System.out.println("������ �����߽��ϴ�!");
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
				// System.out
				// .println(col + "st column " + name + " is JDBC type " + type
				// + " which is called " + typeName);
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