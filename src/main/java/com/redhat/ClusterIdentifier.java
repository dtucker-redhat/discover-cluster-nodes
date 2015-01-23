package com.redhat;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

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

	private static final Logger LOG = LoggerFactory
			.getLogger(ClusterIdentifier.class);

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
			LOG.debug("Found IP: " + ipAddr.getIpAddress().getHostAddress());
			addresses.add(ipAddr);
		}

		return addresses;
	}

	public void getJGroupsViaJmx() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, NullPointerException {
		Object obj = ManagementFactory.getPlatformMBeanServer().getAttribute(
				ObjectName.getInstance("jgroups:type=channel,cluster=\"web\""),
				"View");
		
		System.out.println(obj);
	}
}
