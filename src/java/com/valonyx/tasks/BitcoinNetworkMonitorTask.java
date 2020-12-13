
package com.valonyx.tasks;

/*
import com.google.bitcoin.core.AbstractPeerEventListener;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.params.TestNet3Params;
*/

import com.google.common.util.concurrent.ListenableFuture;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

/**
 *
 * @author
 * marlo
 */
public class BitcoinNetworkMonitorTask
    extends TimerTask {
	
	
	/* removed 8/17/16
    private NetworkParameters params;
    private PeerGroup peerGroup;
	
    private final HashMap<Peer, String> reverseDnsLookups = new HashMap<Peer, String>();
	*/
	
	public BitcoinNetworkMonitorTask() {
        //setupNetwork();
        //setupGUI();
        //peerGroup.start();
	}

	@Override
	public void run() {
		
		System.out.println("BitcoinNetworkMonitorTask.run() invoked");
		
		/* removed 8/17/16

        //params = MainNetParams..get();
		params = TestNet3Params.get();
		//params.
        peerGroup = new PeerGroup(params, null [was forward slash]* no chain *[was forward slash]);
        peerGroup.setUserAgent("PeerMonitor", "1.0");
        peerGroup.setMaxConnections(4);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addEventListener(new AbstractPeerEventListener() {
            @Override
            public void onPeerConnected(final Peer peer, int peerCount) {
				
				//peer.getVersionMessage().toString()
				System.out.println("onPeerConnected invoked >" + peer.toString() + " - " + peer.toString());
				
				//PeerHandler fff;
				//peer.get

				
				
				
                //refreshUI();
                lookupReverseDNS(peer);
            }

            @Override
            public void onTransaction(final Peer peer, Transaction txn) {
				
				System.out.println("onTransaction invoked >" + txn.toString());
				System.out.println("confidence >" + txn.getConfidence().toString());
				
				byte[] arr = txn.bitcoinSerialize();
				String hex_string = BitcoinNetworkMonitorTask.bytesToHex(arr);
				System.out.println("txn hex_string >" + hex_string);

				try {
					System.out.println("Waiting for node to send us the dependencies ...");
					List<Transaction> deps = peer.downloadDependencies(txn).get();
					for (Transaction dep : deps) {
						System.out.println("Got dependency " + dep.getHashAsString());
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
            }

            @Override
            public void onPeerDisconnected(final Peer peer, int peerCount) {
				
				System.out.println("onPeerDisconnected invoked >" + peer.toString());
				
                //refreshUI();
                synchronized (reverseDnsLookups) {
                    reverseDnsLookups.remove(peer);
                }
            }
        });
		
		peerGroup.startAndWait();
		
		
		peerGroup.downloadBlockChain();
		

		*/
				
		
	}

	/*
    private void lookupReverseDNS(final Peer peer) {
        new Thread() {
            @Override
            public void run() {
                // This can take a looooong time.
                String reverseDns = peer.getAddress().getAddr().getCanonicalHostName();
                synchronized (reverseDnsLookups) {
                    reverseDnsLookups.put(peer, reverseDns);
                }
                //refreshUI();
            }
        }.start();
    }
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
*/
	
}
