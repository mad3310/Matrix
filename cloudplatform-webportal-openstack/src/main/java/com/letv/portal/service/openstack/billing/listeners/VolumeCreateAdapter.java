package com.letv.portal.service.openstack.billing.listeners;

/**
 * Created by zhouxianguang on 2015/10/14.
 */
public abstract class VolumeCreateAdapter implements VolumeCreateListener{
    @Override
    public void volumeCreated(String region, String volumeId, int volumeIndex, Object userData) throws Exception {}
}
