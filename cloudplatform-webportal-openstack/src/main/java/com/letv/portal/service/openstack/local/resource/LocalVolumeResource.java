package com.letv.portal.service.openstack.local.resource;

import com.letv.portal.model.cloudvm.CloudvmVolume;
import com.letv.portal.model.cloudvm.CloudvmVolumeStatus;
import com.letv.portal.service.openstack.resource.VolumeAttachmentResource;
import com.letv.portal.service.openstack.resource.VolumeResource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhouxianguang on 2015/10/21.
 */
public class LocalVolumeResource implements VolumeResource {

    private CloudvmVolume cloudvmVolume;
    private Set<VolumeAttachmentResource> attachments;

    public LocalVolumeResource(CloudvmVolume cloudvmVolume) {
        this.cloudvmVolume = cloudvmVolume;
//        CloudvmServer server = cloudvmVolume.getServer();
//        String serverId = cloudvmVolume.getServerId();
//        if (serverId != null && server != null && server.getName() != null) {
//            attachments = new HashSet<VolumeAttachmentResource>();
//            attachments.add(new LocalVolumeAttachmentResource(serverId, server.getName()));
//        }
        if (cloudvmVolume.getServerId() != null && cloudvmVolume.getName() != null) {
            attachments = new HashSet<VolumeAttachmentResource>();
            attachments.add(new LocalVolumeAttachmentResource(cloudvmVolume.getServerId(), cloudvmVolume.getServerName()));
        }
    }

    @Override
    public String getName() {
        return cloudvmVolume.getName();
    }

    @Override
    public String getStatus() {
        if (cloudvmVolume.getStatus() == CloudvmVolumeStatus.WAITING_ATTACHING) {
            return CloudvmVolumeStatus.ATTACHING.toString().toLowerCase();
        }
        return cloudvmVolume.getStatus().toString().toLowerCase();
    }

    @Override
    public int getSize() {
        return cloudvmVolume.getSize();
    }

    @Override
    public String getZone() {
        return null;
    }

    @Override
    public Long getCreated() {
        return cloudvmVolume.getCreateTime().getTime();
    }

    @Override
    public String getVolumeType() {
        return cloudvmVolume.getVolumeType().getDisplayName();
    }

    @Override
    public String getSnapshotId() {
        return cloudvmVolume.getSnapshotId();
    }

    @Override
    public String getDescription() {
        return cloudvmVolume.getDescription();
    }

//    @Override
//    public String getTenantId() {
//        return null;
//    }

    @Override
    public Set<VolumeAttachmentResource> getAttachments() {
        return attachments;
    }

    @Override
    public String getRegionDisplayName() {
        return null;
    }

    @Override
    public String getRegion() {
        return cloudvmVolume.getRegion();
    }

    @Override
    public String getId() {
        return cloudvmVolume.getVolumeId();
    }
}