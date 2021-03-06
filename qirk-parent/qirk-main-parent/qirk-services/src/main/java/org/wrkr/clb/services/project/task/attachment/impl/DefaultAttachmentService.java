package org.wrkr.clb.services.project.task.attachment.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.project.task.attachment.AttachmentRepo;
import org.wrkr.clb.repo.project.task.attachment.TemporaryAttachmentRepo;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.dto.project.task.AttachmentCreateDTO;
import org.wrkr.clb.services.project.task.attachment.AttachmentService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultAttachmentService implements AttachmentService {

    @Autowired
    private AttachmentRepo attachmentRepo;

    @Autowired
    private TemporaryAttachmentRepo temporaryAttachmentRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public AttachmentDTO create(String externalPath, Task task) {
        Attachment attachment = new Attachment();

        attachment.setFilename(ExtStringUtils.substringFromLastSymbol(externalPath, '/'));
        attachment.setExternalPath(externalPath);
        attachment.setTask(task);

        attachment = attachmentRepo.save(attachment);
        return AttachmentDTO.fromEntity(attachment);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public List<AttachmentDTO> createFromTemporary(User currentUser, AttachmentCreateDTO createDTO) throws ApplicationException {
        // security start
        securityService.authzCanUpdateTask(currentUser, createDTO.taskId);
        // security finish

        Long projectId = taskRepo.getProjectIdById(createDTO.taskId);
        if (projectId == null) {
            throw new NotFoundException("Task");
        }
        List<Attachment> attachmentList = attachmentRepo.saveBatch(createDTO.taskId, projectId, createDTO.uuids);
        temporaryAttachmentRepo.deleteByProjectIdAndUuids(projectId, createDTO.uuids);
        return AttachmentDTO.fromEntities(attachmentList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<AttachmentDTO> listByTask(User currentUser, Long taskId) {
        // security start
        securityService.authzCanReadTask(currentUser, taskId);
        // security finish

        List<Attachment> attachmentList = attachmentRepo.listNotDeletedByTaskId(taskId);
        return AttachmentDTO.fromEntities(attachmentList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long id) throws Exception {
        Attachment attachment = attachmentRepo.getNotDeletedByIdAndFetchDropboxSettings(id);
        if (attachment == null) {
            throw new NotFoundException("Attachment");
        } else {
            // security start
            securityService.authzCanUpdateTask(currentUser, attachment.getTaskId());
            // security finish
        }

        // don't delete from YandexCloud
        attachmentRepo.setDeletedToTrue(attachment.getId());
    }

    /*
     * @Override
     * 
     * @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
     * public String getThumbnail(User currentUser, Long id) throws Exception {
     * Attachment attachment = attachmentRepo.getNotDeletedByIdAndFetchDropboxSettings(id);
     * if (attachment == null) {
     * throw new NotFoundException("Attachment");
     * } else {
     * // security start
     * securityService.authzCanReadTask(currentUser, attachment.getTaskId());
     * // security finish
     * }
     * 
     * String thumbnail = dropboxService.getThumbnail(
     * attachment.getDropboxSettings().getToken(), attachment.getPath());
     * return thumbnail;
     * }
     */
}
