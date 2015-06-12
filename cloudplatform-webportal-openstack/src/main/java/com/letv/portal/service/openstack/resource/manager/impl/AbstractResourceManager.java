package com.letv.portal.service.openstack.resource.manager.impl;

import com.letv.portal.service.openstack.exception.RegionNotFoundException;
import com.letv.portal.service.openstack.resource.manager.ResourceManager;

import java.io.Closeable;
import java.text.MessageFormat;

public abstract class AbstractResourceManager implements ResourceManager,
		Closeable {
	protected String endpoint;
	protected String userId;
	protected String password;

	public AbstractResourceManager(String endpoint, String userId, String password) {
		this.endpoint = endpoint;
		this.userId = userId;
		this.password = password;
	}

	public void checkRegion(String region) throws RegionNotFoundException {
		if (!getRegions().contains(region)) {
			throw new RegionNotFoundException(MessageFormat.format(
					"Region \"{0}\" is not found.", region));
		}
	}
}
