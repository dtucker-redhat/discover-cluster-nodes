package com.redhat;

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.Event;
import org.jgroups.PhysicalAddress;
import org.jgroups.stack.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterIdentifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClusterIdentifier.class);

	public List<IpAddress> getJGroupsMembers() {
		Channel channel = (Channel) CurrentServiceContainer
				.getServiceContainer()
				.getService(
						ServiceName.JBOSS.append("jgroups", "channel", "web"))
				.getValue();

		List<Address> members = channel.getView().getMembers();
		List<IpAddress> addresses = new ArrayList<IpAddress>();

		for (Address member : members) {
			PhysicalAddress physicalAddr = (PhysicalAddress) channel
					.down(new Event(Event.GET_PHYSICAL_ADDRESS, member));
			IpAddress ipAddr = (IpAddress) physicalAddr;
			LOG.debug("Found IP: "
					+ ipAddr.getIpAddress().getHostAddress());
			addresses.add(ipAddr);
		}
		
		return addresses;
	}
}
