/**
 * 	Program that runs a GUI multi-player BlackJack game over TCP/IP
 */

public class BlackJack {

	public static void main(String[] args) {
		
		Boolean loggedIn = false;
		Boolean signedUp = false;

		Boolean menuChoiceSelected = false;

		//
		while (!menuChoiceSelected) {
			System.out.println("What would you like to do?");
			System.out.println("1. Login");
			System.out.println("2. Sign Up");
			System.out.println("0. Exit");

			char menuChoice = sc.nextLine().charAt(0);

			switch (menuChoice) {
			case '1':	//login
				menuChoiceSelected = true;
				loggedIn = false;
				while (!loggedIn) {
					//user attempts to login
					loggedIn = userLogin();
				}
				break;
			case '2':	//signup
				menuChoiceSelected = true;
				signedUp = false;
				while (!signedUp) {
					//user attempts to login
					signedUp = userSignUp(socket);
				}
				break;
			case '0':
				menuChoiceSelected = true;
				programRunning = false;
				System.exit(0);	//graceful exit
				break;
			default:	
			}
		}
	}
}