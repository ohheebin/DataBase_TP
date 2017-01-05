import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.driver.OracleDriver;
import java.util.Scanner;

class DAO {
	String DRIVER = "oracle.jdbc.driver.OracleDriver";
	String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
	String USER = "HEEBIN";
	String PASS = "heebin";

	private static Connection conn = null;
	private static DatabaseMetaData meta = null;

	public boolean createConn() {
		try {
			Class.forName(DRIVER);
			System.out.println("[*]	JDBC 드라이버 로드 완료.");
			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("[*]	데이터베이스 접속 완료.");
		} catch (Exception e) {
			System.out.println("[*]	데이터베이스 접속 오류 발생: \n" + e.getMessage());
			return false;
		}

		return true;
	}

	public Connection getConn() {
		return conn;
	}

}

public class Main {

	private static Connection conn01 = null; // 커넥션 정보 저장 객체
	private static DatabaseMetaData meta01 = null; // 데이터베이스 메타정보 저장 객체
	private static Connection conn02 = null;
	private static DatabaseMetaData meta02 = null;

	public static void main(String[] args) throws SQLException {

		int input = 0;
		Customer customer = new Customer();
		Manager manager = new Manager();
		customer.createConn(); // 데이터베이스와 연결 수립
		conn01 = customer.getConn(); // 데이터베이스 연결 정보 취득
		meta01 = customer.getDBMD(conn01);
		manager.createConn();
		conn02 = manager.getConn();
		meta02 = manager.getDBMD(conn02);

		try {
			System.out.println(meta01.getTimeDateFunctions());
			System.out.println(meta01.getUserName());
		} catch (Exception e) {
			System.out.println("[*] customer 메타정보 출력 오류 발생: \n" + e.getMessage());
		}

		try {
			System.out.println(meta02.getTimeDateFunctions());
			System.out.println(meta02.getUserName());
		} catch (Exception e) {
			System.out.println("[*] manager 메타정보 출력 오류 발생: \n" + e.getMessage());
		}

		while (input != 3) {
			System.out.print("Are you manager or customer? (1: manager, 2: customer 3: exit) : ");
			Scanner scan = new Scanner(System.in);
			input = scan.nextInt();
			switch (input) {
			case 1:
				managerScenario(manager, conn02);
				break;
			case 2:
				customerScenario(customer, conn01);
				break;
			case 3:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}

		try {
			conn01.close(); // 커넥션 닫기
			System.out.println("[*] customer 데이터베이스 접속 종료.");
		} catch (SQLException e) {
			System.out.println("[*] customer 오류 발생: \n" + e.getMessage());
		}

		try {
			conn02.close(); // 커넥션 닫기
			System.out.println("[*] manager 데이터베이스 접속 종료.");
		} catch (SQLException e) {
			System.out.println("[*] manager 오류 발생: \n" + e.getMessage());
		}

	}
	//관리자를 선택한 경우 이루어지는 function
	private static void managerScenario(Manager manager, Connection conn) {
		int inputInManager = 0;
		Scanner scanner = new Scanner(System.in);
		while (inputInManager != 8) {
			System.out
					.print("What do you want to do? (1: manage movies, 2: manage cinema, 3: manage Screen 4: manage Screening, "
							+ "5: manage VIP, 6: ticketing, 7: payment, 8: exit) : ");
			inputInManager = scanner.nextInt();
			switch (inputInManager) {
			case 1:
				manager.manageMovie(conn);// 영화등록
				break;
			case 2:
				manager.manageCinema(conn);// 영화관등록
				break;
			case 3:
				manager.manageScreen(conn);// 상영관등록
				break;
			case 4:
				manager.manageScreening(conn);// 상영등록
				break;
			case 5:
				manager.manageVIP(conn);// vip관리
				break;
			case 6:
				manager.ticketing(conn);// 티켓
				break;
			case 7:
				manager.helpPayment(conn);// 지불
				break;
			case 8:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}

		}
	}
	//고객을 선택한 경우 이루어지는 function
	private static void customerScenario(Customer customer, Connection conn) {
		int inputInCustomer = 0;
		Scanner scanner = new Scanner(System.in);
		while (inputInCustomer != 3) {
			System.out.print("What do you want to do? (1: log-in, 2: sign up, 3: exit) : ");
			inputInCustomer = scanner.nextInt();
			switch (inputInCustomer) {
			case 1:
				boolean successToLogin = customer.login(conn);
				if (successToLogin) {
					int inputInLogIn = 0;
					while (inputInLogIn != 5 && inputInLogIn != 6) {
						System.out.print("What do you want to do? (1: booking a movie, 2: search movies, "
								+ "3: modify your information, 4: check reservation, 5: secession, 6:log-out) : ");
						inputInLogIn = scanner.nextInt();
						switch (inputInLogIn) {
						case 1:
							customer.bookingMovie(conn);
							break;
						case 2:
							customer.SearchMovie(conn);
							break;
						case 3:
							customer.modifyMyInformation(conn);
							break;
						case 4:
							customer.checkMyReservation(conn);
							break;
						case 5:
							customer.secession(conn);
							break;
						case 6:
							System.out.println("Complete log out");
							break;
						default:
							System.out.println("Wrong input!!!");
							break;
						}
					}
				}
				break;
			case 2:
				boolean result = false;
				while (result == false) {
					result = customer.signUp(conn);
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
}