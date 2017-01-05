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

	String inputtedUserID;//로그인한 회원 ID를 저장하기 위해 만든 변수
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
	//회원의 ID를 입력받고 테이블에 존재하면 password를 확인하고 같은 튜블에 존재하면 로그인을 한다.
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
			// System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
			return false;
		}

	}
	//고객의 회원가입 ID의 중복을 확인하고 테이블에 존재하지 않는 경우에는 회원가입을 진행한다.
	public boolean signUp(Connection conn) {
		String inputID = "";
		String inputName, inputPW, inputPhoneNumber, inputAddress, inputBirth;
		boolean nobodyUseThisID = false;
		while (nobodyUseThisID == false) {
			System.out.print("ID : ");
			inputID = scan.nextLine();
			//고객 테이블에 회원 ID의 유무를 확인한다.
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
	//예매를 하는 함수 결제 전의 과정까지 진행한다.
	public void bookingMovie(Connection conn) {
		String movieId, movieName, screenNum = "";
		String inputCinemaName, inputMovieName, inputSeatNum;
		int reservationSeatCount = 0, remainSeat, screenTotalSeatNumber, reservationNumber = 0;
		String inputScreeningNumber;
		//영화관의 이름들을 출력시켜서 영화관을 선택한다.
		try {
			ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
			System.out.println("=================================================");
			while (rs.next()) {
				System.out.println("영화관 이름 : " + rs.getString(1));
			}

		} catch (Exception e) {
			System.out.println("영화관이 존재하지 않습니다.");
		}
		System.out.println("----------------------------------------------------");
		System.out.print("영화관 선택 : ");
		inputCinemaName = scan.nextLine();
		// 영화관을 선택하면 그 영화관에서 상영중인 영화를 보여준다.
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
			System.out.println("그 영화관에는 영화가 상영하지 않습니다.");
		}
		System.out.println("----------------------------------------------------");
		// 영화관에서 상영중인 영화를 선택하면 영화 상영일정들을 보여주고 상영 번호를 선택한다.
		System.out.print("영화 선택 : ");
		inputMovieName = scan.nextLine();
		try {
			ResultSet rs = this.select(conn,
					"SELECT SCREENING_TIME, MOVIE_ID, SCREENING_NUMBER, SCREEN_NUMBER FROM SCREENING WHERE CINEMA_NAME = '"+inputCinemaName+"' AND MOVIE_ID = ("
							+ "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_NAME = '" + inputMovieName + "')");
			System.out.println("----------------------------------------------------");
			System.out.println("선택한 영화 : " + inputMovieName);
			rs.next();
			do {
				screenNum = rs.getString(4);
				System.out.println("상영번호 : " + rs.getString(3) + "\t상영일정 : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("선택한 영화가 잘 못 됐습니다.");
		}
		System.out.println("----------------------------------------------------");

		System.out.print("상영번호 선택 : ");
		inputScreeningNumber = scan.nextLine();

		// 예매할 매수를 선택한다.
		try {
			//선택한 영화관의 상영관 정보 중 상영관의 전체 좌석 수를 받아온다.
			ResultSet screen = this.select(conn, "SELECT SCREEN_TOTAL_SEAT_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = "
					+ screenNum + " AND CINEMA_NAME = '" + inputCinemaName + "'");
			screen.next();
			screenTotalSeatNumber = Integer.parseInt(screen.getString(1));
			//선택한 상영관의 남은 좌석 정보를 받아온다.
			ResultSet rs = this.select(conn,
					"SELECT REMAIN_SEAT_NUMBER FROM SCREENING WHERE SCREENING_NUMBER = " + inputScreeningNumber);
			rs.next();
			remainSeat = Integer.parseInt(rs.getString(1));//상영관의 남은 좌석을 저장하는 변수
			System.out.print("예매할 매수 선택(남은 좌석 수 : " + remainSeat + ") : ");
			reservationSeatCount = scan.nextInt();
			scan.nextLine();
			int temp = reservationSeatCount;
			// 예매한 매수만큼 좌석을 선택한다. 이미 예매된 좌석에 대한 정보를 출력시키고 예매한 수만큼 좌석을 선택한다.
			System.out.println("----------------------------------------------------");
			if (reservationSeatCount <= remainSeat) {
				int[] checkRemainSeat = new int[100];//예매된 좌석에 대한 정보를 확인하기위한 변수
				for (int i = 0; i < reservationSeatCount;) {
					System.out.println("좌석선택<1~" + screenTotalSeatNumber + "> ");//전체 좌석수를 보여 준다.
					try {
						ResultSet rt = this.select(conn,
								"SELECT SEAT_NUMBER FROM TICKET WHERE SCREENING_NUMBER = " + inputScreeningNumber);
						rt.next();
						do {
							checkRemainSeat[Integer.parseInt(rt.getString(1))] = 1;
						} while (rt.next());
						//예매된 좌석에 대한 정보를 보여준다.
						System.out.print("<예매된 좌석(");
						for (int j = 0; j < checkRemainSeat.length; j++) {
							if (checkRemainSeat[j] == 1)
								System.out.print(j + " ");
						}
						//좌석 선택후 남은 예매 수에 대해 보여준다.
						System.out.print("), 남은 예매 수(" + (reservationSeatCount - i) + ")> : ");
					} catch (Exception e) {
						System.out.print("<남은 예매 수(" + (reservationSeatCount - i) + ")> : ");
					}
					inputSeatNum = scan.nextLine();//좌석을 선택
					//예매 테이블을 insert하고 티켓 테이블에 입력한 값들을 insert한다 그리고 여러장을 예매했다면 다시 좌석 선택으로 돌아간다.
					//고객 테이블에 존재하는 ticketing수를 1증가 시키고 상영 테이블의 남은 좌석을 1 감소 시킨다.
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
							System.out.println("예약테이블시퀀스 오류");
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
						System.out.println("티켓테이블 오류");
					}
				}
				this.doPayment(conn, reservationSeatCount, reservationNumber);//결제 function을 불러온다.
			} else {
				System.out.println("남은 좌석이 부족합니다.");
			}
		} catch (Exception e) {
			System.out.println("그 일정에는 영화 상영이 없습니다.");
		}
		System.out.println("=================================================");
	}
	
	//결제 선택 예약이 끝나고 난후 실행
	private void doPayment(Connection conn, int numOfTicket, int reservationNum) {
		int inputMethodToPayment = 0;
		int point;
		int usedPoint = 0;//사용된 포인트
		int totalCost = 10000 * numOfTicket;//예매의 총가격 한장의 티켓당 10000원
		//현장 결제인지 인터넷 결제인지 선택하고 인터넷 결제이면 결제를 실행 현장 결제이면 manager를 통해 결제를 하게 한다.
		while (inputMethodToPayment != 1 && inputMethodToPayment != 2) {
			System.out.print("결제하는 방법은? (1: 현장 결제, 2: 인터넷 결제) : ");
			inputMethodToPayment = scan.nextInt();
			scan.nextLine();
			switch (inputMethodToPayment) {
			//현장 결제인 경우 manager가 결제를 처리한다.
			case 1:
				break;
			//인터넷 결제
			case 2:
				try {
					ResultSet rs = this.select(conn,
							"SELECT CUSTOMER_POINT FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					rs.next();
					point = Integer.parseInt(rs.getString(1));
				} catch (Exception e) {
					System.out.println("고객 테이블 에러");
					break;
				}
				System.out.println("인터넷 결제를 시작합니다.");
				System.out.println("당신의 총 결제 금액은 " + totalCost + "입니다.");
				System.out.println("당신의 포인트는 " + point + "p 있습니다.");
				//포인트가 1000점인 경우에만 포인트를 사용하게 한다.
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
								//고객 테이블에 존재하는 point와 사용할 포인트를 비교해서 초과하지 않게 처리한다.
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
				//총 가격과 포인트사용량을 체크해서 총 지불해야할 가격을 지정한다.
				int cash = totalCost - usedPoint;
				//PAYMENT테이블에 값들을 insert한다 마지막이 0인 이유는 발권 여부를 확인해주기 위해 만든 속성이다.
				this.insert(conn, "INSERT INTO PAYMENT VALUES( PAYMENTNUM.NEXTVAL," + usedPoint + "," + cash + ",'"
						+ inputtedUserID + "'," + reservationNum + ",0)");
				int addPoint = numOfTicket * 100;//티켓한장당 100의 point를 준다.
				//포인트 정보를 고객 테이블에 update한다.
				this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT+" + addPoint
						+ "WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				System.out.println("결제에 성공했습니다!");
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}
	//예매율에 대한 영화 차트를 보여준다.
	public void SearchMovie(Connection conn) {
		try {
			//예매율에 따라 영화 정보를 높은 순으로 보여준다.
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_NAME, RESERVATION_RATE FROM MOVIE ORDER BY RESERVATION_RATE DESC");
			System.out.println("=================================================");
			System.out.printf("%30s		|%10s\n", "영화 제목", "예매율");
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
	//회원 정보를 수정하는 function
	public void modifyMyInformation(Connection conn) {
		try {
			int inputForModify = 0;

			System.out.println(inputtedUserID);//현재 로그인된 회원의 ID
			ResultSet rs = this.select(conn, "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			//현재 로그인된 회원의 정보를 출력시켜준다.
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
			//수정하고자 하는 부분을 선택해 수정을 한다.
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
	//현재 로그인된 회원에 대한 예매 정보, 예매 삭제를 하는 function
	public void checkMyReservation(Connection conn) {
		int indexForReservation = 0;
		try {
			//로그인된 회원이 예매한 예약번호, 영화제목, 영화관, 상영시간, 상영관을 보여준다.
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_ID, RESERVATION_NUMBER, CINEMA_NAME, SCREENING_TIME, SCREEN_NUMBER FROM RESERVATION, SCREENING WHERE RESERVATION.CUSTOMER_ID = '"
							+ inputtedUserID + "' AND SCREENING.SCREENING_NUMBER = RESERVATION.SCREENING_NUMBER");
			System.out.println(
					"==================================================================================================");
			System.out.printf("%10s      %20s       %10s        %15s        %5s\n", "예약번호", "영화 제목", "영화관", "상영 시간",
					"상영관");
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
		//예매에 대해서 몇장의 티켓을 예매했는지와 예매 취소를 선택한다.
		while (indexForReservation != 3) {
			System.out.println(
					"What do you want to do? (1: show tickets of reservation, 2: delete reservation, 3: exit) : ");
			indexForReservation = scan.nextInt();
			scan.nextLine();
			String inputReservationNumber;
			switch (indexForReservation) {
			//한번의 예매에서 몇장의 티켓을 샀는지 보여준다.
			case 1:
				System.out.println("What reservation do you want to see more? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				try {
					ResultSet rs = this.select(conn,
							"SELECT * FROM TICKET WHERE RESERVATION_NUMBER = " + inputReservationNumber);
					System.out.println(
							"==================================================================================================");
					System.out.printf("%10s      %10s       %10S        %10S        %5S\n", "티켓번호", "좌석번호", "예약번호",
							"영화관", "상영관");
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
			//예매 취소
			case 2:
				String screeningNumber = "";
				System.out.println("What reservation do you want to delete? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				//취소할 예매 번호를 선택하고 선택한 예매번호와 일치하는 예매 테이블과 티켓 테이블의 정보를 삭제한다.
				//예매 취소를 하면 point를 티켓수*100만큼 감소 시키고 고객의 티켓팅수를 감소 시킨다.
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
	//회원 탈퇴 function
	public void secession(Connection conn) {
		int inputForsecession;
		System.out.print("Are you sure to remove your account? (1: Yes, 2: No!) : ");
		inputForsecession = scan.nextInt();
		scan.nextLine();
		switch (inputForsecession) {
		//현재 로그인된 고객에 대한 튜플을 고객 테이블에서 삭제 시킨다.
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
				// System.out.println(col + "st column " + name + " is JDBC type
				// " + type + " which is called " + typeName);
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