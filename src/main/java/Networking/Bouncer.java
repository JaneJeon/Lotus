package Networking;/*
 * @author: Sungil Ahn
 */

import static Utils.LoadResource.getTime;

// checks asynchronously whether we're actually connected to a player, so the server can continue listening
// this has to be done to prevent players from being matched up to bots/crawlers
public class Bouncer {
	private static final double gracePeriod = 1.0;

	public Bouncer(Server server, ServerCommunicator comm) throws InterruptedException {
		boolean verified = false;
		double startTime = System.nanoTime();

		// give a little time to check the validity of the person connected
		while ((System.nanoTime() - startTime) / 1_000_000_000 < gracePeriod) {
			if (comm.getFirstMsgStatus() == true) {
				server.addToList(comm);
				verified = true;
				break;
			}
			// check every 0.1 s
			Thread.sleep(100);
		}

		// destroy the intruder - let the garbage collector handle disposing of this comm
		if (!verified) {
			comm.close();
			System.out.println("Intruder detected!");
			server.getLog().warn(getTime() + " > " + "Intruder detected!");
		}

		// now kill itself
		server.killBouncer(this);
	}
}