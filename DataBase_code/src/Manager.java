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
			System.out.println("[*] JDBC 드라이버 로드 완료.");
			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("[*] 데이터베이스 접속 완료.");
		} catch (Exception e) {
			System.out.println("[*] 데이터베이스 접속 오류 발생: \n" + e.getMessage());
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
			System.out.println("[*] DBMD 오류 발생: \n" + e.getMessage());
		}

		return meta;
	}

	// 영화 등록, 수정, 삭제를 하는 함수
	public void manageMovie(Connection conn) {
		int inputForManageMovie = 0;
		String movieId;
		String name, id;
		String MovieId, MovieName, information, director, grade, MovieCast;
		int reservationRate, runingtime;

		while (inputForManageMovie != 4) {
			System.out.print("What do you want to do? (1: 영화 등록, 2: 영화 삭제, 3: 영화 정보 수정, 4: exit) : ");
			inputForManageMovie = scan.nextInt();
			scan.nextLine();

			switch (inputForManageMovie) {
			// 영화등록 영화ID, 영화제목, 러닝타임, 영화정보, 등급, 감독, 배우를 입력해서 테이블에 저장시킨다.
			case 1:
				System.out.println("=================================================");
				System.out.print("영화ID? : ");
				MovieId = scan.nextLine();
				System.out.print("영화제목? : ");
				MovieName = scan.nextLine();
				System.out.print("RuningTime? : ");
				runingtime = scan.nextInt();
				scan.nextLine();
				System.out.print("영화정보? : ");
				information = scan.nextLine();
				System.out.print("GRADE?(전체, 12, 15, 19) : ");
				grade = scan.nextLine();
				System.out.print("감독? : ");
				director = scan.nextLine();
				System.out.print("배우? : ");
				MovieCast = scan.nextLine();
				System.out.println("=================================================");
				insert(conn, "INSERT INTO MOVIE VALUES('" + MovieId + "','" + MovieName + "'," + 0 + "," + runingtime
						+ ",'" + information + "','" + grade + "','" + director + "','" + MovieCast + "')");
				break;
			// 등록된 영화 삭제 영화ID를 입력해서 등록된 영화정보를 삭제한다.
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
					System.out.println("등록된 영화가 없습니다. ");
				}
				System.out.println("=================================================");
				System.out.print("삭제할 영화ID? : ");
				movieId = scan.nextLine();
				// 삭제할 영화ID를 입력해서 테이블에 존재하면 DELETE 존재하지 않은 경우에는 오류를 출력시킨다.
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
					System.out.println("삭제할 영화가 없습니다.");
				}
				break;
			// 영화정보에 대해서 수정한다. 수정할 영화 ID를 선택해서 영화 테이블에 존재하는 영화 ID를 제외하고 나머지 정보들을
			// 수정할 수 있다.
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
					System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
				}
				System.out.println("=================================================");
				System.out.print("수정할 영화ID? : ");
				movieId = scan.nextLine();
				// 입력한 영화ID와 같은 영화ID가 테이블에 존재할 경우 영화 정보를 수정하고 없다면 오류를 출력시킨다.
				try {
					ResultSet rs = this.select(conn, "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_ID = '" + movieId + "'");
					rs.next();
					id = rs.getString(1);
					// 영화 정보 수정 선택
					if (movieId.equals(id)) {
						while (inputForModifyMovieInformation != 8) {
							System.out
									.print("무엇을 수정하시겠습니까? (1: MOVIE_NAME, 2: RESERVATION_RATE, 3: RUNINGTIME, 4: INFORMATION"
											+ ", 5: GRADE, 6: DIRECTOR, 7: MOVIE_CAST, 8: exit) : ");
							inputForModifyMovieInformation = scan.nextInt();
							scan.nextLine();
							switch (inputForModifyMovieInformation) {
							case 1:
								System.out.print("수정할 MOVIE_NAME? : ");
								MovieName = scan.nextLine();
								update(conn, "UPDATE MOVIE SET MOVIE_NAME = '" + MovieName + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 2:
								System.out.print("수정할 RESERVATION_RATE?(100이하) : ");
								reservationRate = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE MOVIE SET RESERVATION_RATE = " + reservationRate
										+ " WHERE MOVIE_ID = '" + movieId + "'");
								break;
							case 3:
								System.out.print("수정할 RUNINGTIME? : ");
								runingtime = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE MOVIE SET RUNINGTIME = " + runingtime + " WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 4:
								System.out.print("수정할 INFORMATION? : ");
								information = scan.nextLine();
								update(conn, "UPDATE MOVIE SET INFORMATION = '" + information + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 5:
								System.out.print("수정할 GRADE?(전체, 12, 15, 19) : ");
								grade = scan.nextLine();
								update(conn,
										"UPDATE MOVIE SET GRADE = '" + grade + "' WHERE MOVIE_ID = '" + movieId + "'");
								break;
							case 6:
								System.out.print("수정할 DIRECTOR? : ");
								director = scan.nextLine();
								update(conn, "UPDATE MOVIE SET DIRECTOR = '" + director + "' WHERE MOVIE_ID = '"
										+ movieId + "'");
								break;
							case 7:
								System.out.print("수정할 MOVIE_CAST? : ");
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
				} catch (Exception e) {// 입력한 영화ID가 DB에 존재하지 않는경우
					System.out.println("입력한 영화ID가 존재하지 않습니다.");
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

	// 영화관 정보 등록, 삭제, 수정
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
			case 1:// 영화관등록
				System.out.print("영화관이름? : ");
				cinemaName = scan.nextLine();
				System.out.print("영화관주소? : ");
				cinemaAddress = scan.nextLine();
				System.out.print("영화관전화번호? : ");
				cinemaPhonenumber = scan.nextLine();
				System.out.print("영화관총좌석수? : ");
				cinemaTotalSeatNumber = scan.nextInt();
				scan.nextLine();
				System.out.print("영화관 전체 상영관수? : ");
				cinemaScreenNumber = scan.nextInt();
				scan.nextLine();

				this.insert(conn, "INSERT INTO CINEMA VALUES('" + cinemaName + "','" + cinemaAddress + "','"
						+ cinemaPhonenumber + "'," + cinemaTotalSeatNumber + "," + cinemaScreenNumber + ")");
				System.out.println("=================================================");
				break;

			case 2:// 영화관삭제

				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					rs.next();
					do {
						name = rs.getString(1);
						System.out.println("CinemaName : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
				}

				System.out.print("삭제할 영화관이름? : ");
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
					System.out.println("삭제할 영화관이 존재하지 않습니다.");
				}
				break;

			case 3:// 영화관수정
				int inputForModifyCinemaInformation = 0;
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					rs.next();
					do {
						name = rs.getString(1);
						System.out.println("CINEMA_NAME : " + name);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
				}

				System.out.print("수정할 영화관이름? : ");
				cinemaName = scan.nextLine();
				// 수정할 영화관을 선택하고 없으면 오류를 출력시킨다.
				try {
					ResultSet rs = this.select(conn,
							"SELECT CINEMA_NAME FROM CINEMA WHERE CINEMA_NAME = '" + cinemaName + "'");
					rs.next();
					name = rs.getString(1);
					if (cinemaName.equals(name)) {
						// 수정할 것을 선택한다.
						while (inputForModifyCinemaInformation != 5) {
							System.out
									.print("무엇을 수정하시겠습니까? (1: CINEMA_ADDRESS, 2: CINEMA_PHONENUMBER , 3: CINEMA_TOTAL_SEAT_NUMBER "
											+ "4: TOTAL_SCREEN_NUMBER, 5: exit) : ");
							inputForModifyCinemaInformation = scan.nextInt();
							scan.nextLine();
							switch (inputForModifyCinemaInformation) {
							case 1:
								System.out.print("수정할 CINEMA_ADDRESS? : ");
								cinemaAddress = scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_ADDRESS = '" + cinemaAddress
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 2:
								System.out.print("수정할 CINEMA_PHONENUMBER? : ");
								cinemaPhonenumber = scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_PHONENUMBER = '" + cinemaPhonenumber
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 3:
								System.out.print("수정할 CINEMA_TOTAL_SEAT_NUMBER? : ");
								cinemaTotalSeatNumber = scan.nextInt();
								scan.nextLine();
								update(conn, "UPDATE CINEMA SET CINEMA_TOTAL_SEAT_NUMBER = '" + cinemaTotalSeatNumber
										+ "' WHERE CINEMA_NAME = '" + cinemaName + "'");
								break;
							case 4:
								System.out.print("수정할 TOTAL_SCREEN_NUMBER? : ");
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
					System.out.println("수정할 영화관이 존재하지 않습니다.");
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

	// 상영관은 지정하면 수정을 하지않고 삭제를 하지 못한다.
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
			case 1:// 상영관등록
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("CinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
				}
				System.out.println("=================================================");
				System.out.print("상영관을 추가할 영화관 선택 : ");
				cinemaName = scan.nextLine();
				// 상영관을 추가할 영화관이 없다면 오류를 출력한다.
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
						// 영화관의 전체 좌석수를 상영관을 추가할때마다 감소 시킨다.
						totalSeatNumberOfEachCinema = totalSeatNumberOfEachCinema - sumSeatNum;
						System.out.println("Total Screen Number : " + totalScreenNumberOfEachCinema
								+ "\nLeft Total Seat Number : " + totalSeatNumberOfEachCinema);
					} while (rs.next());
					System.out.println("=================================================");
				} catch (Exception e) {
					System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
				}
				tempScreen = totalScreenNumberOfEachCinema - num;// 영화관의 전체
																	// 상영관에서 남은
																	// 상영관이 몇개인지
																	// 보여주기 위해
																	// 만든 변수
				int i = 0;
				int[] madeScreen = new int[totalScreenNumberOfEachCinema + 1];// 만들어진
																				// 상영관을
																				// 구별하기
																				// 위해
																				// 만든
																				// 변수,
																				// 만들어져있다면
																				// 1로
																				// 저장한다.
				while (i < tempScreen) {
					do {
						System.out.print("<만들어진 상영관 : ");
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
						System.out.print("상영관 번호(Number) : ");
						screenNumber = scan.nextInt();
						scan.nextLine();
						if (screenNumber > totalScreenNumberOfEachCinema) {// 영화관의
																			// 전체
																			// 좌석수를
																			// 초과하지
																			// 않게끔
																			// 하기
																			// 위해
																			// 만든
																			// if문
							System.out.println("없는 상영관입니다. 다시입력하세요.");
						} else if (madeScreen[screenNumber] != 0) {
							System.out.println("이미 만들어져 있는 상영관입니다. 다시 입력 하세요.");
						} else {
							do {
								System.out.print("원하는 좌석 수 <남은 전체 좌석수(" + totalSeatNumberOfEachCinema + ")> : ");
								screenTotalSeatNumber = scan.nextInt();
								scan.nextLine();
								if (screenTotalSeatNumber > totalSeatNumberOfEachCinema) {
									System.out.println("좌석이 초과 되었습니다. 다시 입력하세요");
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

	// 상영은 등록과 삭제가 있다.
	public void manageScreening(Connection conn) {
		int inputForManageScreening = 0, screenTotalSeat = 0;
		String cinemaName, movieId, screeningTime, movieName;
		String screenNumber, screeningNumber;

		while (inputForManageScreening != 3) {
			System.out.print("What do you want to do? (1: 상영 정보 등록, 2: 상영 정보 삭제, 3: exit) : ");
			inputForManageScreening = scan.nextInt();
			scan.nextLine();
			switch (inputForManageScreening) {
			case 1:// 상영등록
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("cinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("상영할 영화관이 없습니다.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("상영을 추가할 영화관 선택 : ");
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
					System.out.println("이 영화관에는 상영할 상영관이 없습니다.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("상영할 상영관 선택 : ");
				screenNumber = scan.nextLine();
				// 선택한 상영관이 테이블에 존재하지 않는 경우 오류 발생
				try {
					ResultSet rs = this.select(conn,
							"SELECT SCREEN_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = " + screenNumber);
					rs.next();
					rs.getString(1);
				} catch (Exception e) {
					System.out.println("상영할 상영관을 잘못 선택했습니다.");
					break;
				}
				// 상영테이블에 존재하는 그 영화관과 선택한 상영관의 상영일정을 보여준다.
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
					System.out.println("해당 상영관에는 상영일정이 없습니다.");
				}
				System.out.println("=================================================");
				System.out.print("상영 일정 입력 (e.g 2013/12/12 15:20:00) : ");
				screeningTime = scan.nextLine();
				// 상영을 할 영화 선택
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
					System.out.println("상영할 영화가 없습니다.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("상영할 영화ID 선택 : ");
				movieId = scan.nextLine();
				// 상영관의 정보를 받아와서 상영 테이블에 정보를 insert한다.
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

			case 2:// 상영일정삭제
				try {
					ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
					System.out.println("=================================================");
					rs.next();
					do {
						cinemaName = rs.getString(1);
						System.out.println("cinemaName : " + cinemaName);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("상영일정을 삭제할 영화관이 없습니다.");
					break;
				}
				System.out.println("=================================================");
				System.out.print("상영 일정을 삭제할 영화관 선택 : ");
				cinemaName = scan.nextLine();
				System.out.println("=================================================");
				// 선택한 영화관에 상영일정이 없다면 오류를 출력시킨다.
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
								+ "관" + "\tscreeningTime : " + screeningTime);
					} while (rs.next());
				} catch (Exception e) {
					System.out.println("이 영화관에는 상영하는 상영관이 없습니다.");
					System.out.println("=================================================");
					break;
				}
				System.out.println("=================================================");
				System.out.print("상영 일정을 삭제할 상영 번호 선택  : ");
				screeningNumber = scan.nextLine();
				// 삭제할 상영번호를 입력 받아서 상영 일정을 삭제 시킨다.
				try {
					delete(conn, "DELETE FROM SCREENING WHERE SCREENING_NUMBER = '" + screeningNumber + "'");
					System.out.println("delete success!");
				} catch (Exception e) {
					System.out.println("삭제할 상영 번호를 잘못 입력했습니다.");
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

	// vip관리 고객이 예매한 티켓수를 많은 순서부터 10명까지 출력시킨다.
	public void manageVIP(Connection conn) {
		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_ID, CUSTOMER_TIKETING_NUMBER FROM CUSTOMER ORDER BY CUSTOMER_TIKETING_NUMBER DESC");
			System.out.println("=================================================");
			System.out.printf("%30s      |%10s\n", "고객 ID", "티켓예매횟수");
			System.out.println("-------------------------------------------------");
			rs.next();
			int i = 1;//10명까지 출력하기위해 만든 변수
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

	// 발권 결제를 완료한 회원의 정보가 결제 테이블에 저장되기에 결제 테이블에 저장된 것들만 발권을 할 수 있게 한다.
	public void ticketing(Connection conn) {
		String inputtedUserID = "";
		int paymentNum = 0;
		System.out.println("=================================================");
		boolean checkUserID = false;
		// 고객 ID 출력 후 발권을 할 고객 ID 선택
		try {
			ResultSet rs = this.select(conn, "SELECT CUSTOMER_ID FROM CUSTOMER");
			rs.next();
			do {
				System.out.println("고객 ID : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("회원이 존재하지 않습니다.");
		}
		while (checkUserID != true) {
			System.out.println("=================================================");
			System.out.print("발권을 원하는 고객의 ID 입력하세요. : ");
			inputtedUserID = scan.nextLine();
			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				rs.next();
				rs.getString(1);
				checkUserID = true;
			} catch (Exception e) {
				System.out.println("없는 ID입니다. 다시 입력하세요. : ");
			}
		}
		// 결제(PAYMENT)테이블에 존재하는 결제 번호 중 발권 유무를 확인하는 TICKETING이 0인 경우 들만 출력시켜서
		// 발권을 하게 한다.
		try {
			ResultSet rs = this.select(conn,
					"SELECT * FROM PAYMENT WHERE CUSTOMER_ID = '" + inputtedUserID + "' AND TICKETING = 0");
			rs.next();
			do {
				System.out.println("결제 번호 : " + rs.getString(1) + ", 예약 번호 : " + rs.getString(5));
			} while (rs.next());
			System.out.print("발권을 원하는 결제 번호를 입력하세요 : ");
			paymentNum = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			System.out.println("결제내역이 없습니다. ");
			return;
		}
		// 발권이 완료되면 TICKETING을 1로 update하고 발권이 완료된다.
		update(conn, "UPDATE PAYMENT SET TICKETING = 1 WHERE PAYMENT_NUMBER = " + paymentNum);
		System.out.println("결제번호 " + paymentNum + "의 발권이 완료 되었습니다.");
	}

	// 결제 현장 결제를 선택한 고객에 대해서 결제를 해준다.
	public void helpPayment(Connection conn) {
		String inputtedUserID = "";
		int reservationNum = 0, point = 0, totalCost = 0, usedPoint = 0, ticketCount = 0;
		boolean checkUserID = false;
		// 고객들의 ID를 출력시키고 현장 결제를 선택한 고객을 선택
		try {
			System.out.println("=================================================");
			ResultSet rs = this.select(conn, "SELECT CUSTOMER_ID FROM CUSTOMER");
			rs.next();
			do {
				System.out.println("고객 ID : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("회원이 존재하지 않습니다.");
		}
		while (checkUserID != true) {
			System.out.println("=================================================");
			System.out.print("결제를 원하는 고객의 ID 입력하세요. : ");
			inputtedUserID = scan.nextLine();
			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				rs.next();
				rs.getString(1);
				checkUserID = true;
			} catch (Exception e) {
				System.out.println("없는 ID입니다. 다시 입력하세요. : ");
			}
		}
		// 결제 테이블에 존재하지 않는 예매는 현장 결제를 선택한 고객이기에 결제 테이블에 존재하지 않고 예매 테이블에 존재하는 예매
		// 번호를 출력시킨다.
		try {
			ResultSet rs = this.select(conn, "SELECT RESERVATION_NUMBER FROM RESERVATION WHERE CUSTOMER_ID = '"
					+ inputtedUserID + "' AND RESERVATION_NUMBER NOT IN (SELECT RESERVATION_NUMBER FROM PAYMENT)");
			rs.next();
			do {
				System.out.println("예약 번호 : " + rs.getString(1));
			} while (rs.next());
			System.out.print("결제를 원하는 예약 번호를 입력하세요 : ");
			reservationNum = scan.nextInt();
			scan.nextLine();
		} catch (Exception e) {
			System.out.println("예약내역이 없습니다. ");
			return;
		}
		// 한 예매에 존재하는 티켓의 수를 확인하고 총가격을 측정한다.
		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_POINT FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			point = Integer.parseInt(rs.getString(1));
			ResultSet rt = this.select(conn, "SELECT * FROM TICKET WHERE RESERVATION_NUMBER = " + reservationNum);
			while (rt.next()) {
				ticketCount++;
			}
			totalCost = ticketCount * 10000;// 총 예매 가격을 나타내기 위한 변수
		} catch (Exception e) {
			System.out.println("고객 테이블 에러");
			return;
		}
		System.out.println("현장 결제를 시작합니다.");
		System.out.println("당신의 총 결제 금액은 " + totalCost + "입니다.");
		System.out.println("당신의 포인트는 " + point + "p 있습니다.");
		// 현재 고객 테이블에 존재하는 point가 1000점 이상인 경우만 point를 사용하게 한다.
		if (point >= 1000) {
			int tempInput = 0;
			while (tempInput != 1 && tempInput != 2) {
				System.out.print("포인트를 사용하시겠습니까? (1:Yse, 2:No) : ");
				tempInput = scan.nextInt();
				scan.nextLine();
				switch (tempInput) {
				case 1:
					boolean tempUseCheck = false;
					while (tempUseCheck == false) {
						System.out.print("얼마의 포인트를 사용하시겠습니까? : ");
						usedPoint = scan.nextInt();
						scan.nextLine();
						if (usedPoint <= point) {
							tempUseCheck = true;
						} else {
							System.out.println("사용할 수 있는 포인트를 초과했습니다. 다시 입력하세요");
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
			System.out.println("포인트 부족으로 결제에는 사용하실 수 없습니다. ");
		}
		// 사용한 point만큼의 point를 줄이고 결제한 만큼의 point를 증가시키고 ticketing(발권유무)을 0으로
		// PAYMENT테이블에 insert한다.
		// 그리고 현재 남은 point를 고객 테이블에 update해준다.
		int cash = totalCost - usedPoint;
		this.insert(conn, "INSERT INTO PAYMENT VALUES( PAYMENTNUM.NEXTVAL," + usedPoint + "," + cash + ",'"
				+ inputtedUserID + "'," + reservationNum + ",0)");
		int addPoint = ticketCount * 100;
		this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT+" + addPoint + " WHERE CUSTOMER_ID = '"
				+ inputtedUserID + "'");
		System.out.println("결제에 성공했습니다!");
	}

	public boolean insert(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			int rowCount = stmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("데이터 삽입 실패");
				return false;
			} else {
				System.out.println("데이터 삽입 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] INSERT 오류 발생: \n" + e.getMessage());
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

			// 질의 결과 메타 정보 추출
			for (int col = 1; col <= rsMeta.getColumnCount(); col++) {
				int type = rsMeta.getColumnType(col);
				String typeName = rsMeta.getColumnTypeName(col);
				String name = rsMeta.getColumnName(col);
				// System.out
				// .println(col + "st column " + name + " is JDBC type " + type
				// + " which is called " + typeName);
			}

			// 질의 결과 반환
			return rs;
		} catch (Exception e) {
			System.out.println("[*] SELECT 오류 발생: \n" + e.getMessage());
		}
		return rs;
	}

	public boolean update(Connection conn, String query) {
		try {
			Statement pstmt = conn.createStatement();

			int rowCount = pstmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("데이터 수정 실패");
			} else {
				System.out.println("데이터 수정 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] UPDATE 오류 발생: \n" + e.getMessage());
		}
		return true;
	}

	public boolean delete(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			int rowCount = stmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("데이터 삭제 실패");
				return false;
			} else {
				System.out.println("데이터 삭제 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] DELETE 오류 발생: \n" + e.getMessage());
		}
		return true;
	}
}