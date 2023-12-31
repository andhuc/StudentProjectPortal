package swp.studentprojectportal.service.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.studentprojectportal.model.Milestone;
import swp.studentprojectportal.model.Submission;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.repository.IMilestoneRepository;
import swp.studentprojectportal.repository.IProjectRepository;
import swp.studentprojectportal.repository.ISubmissionRepository;
import swp.studentprojectportal.service.ISubmissionService;

import java.util.List;

@Service
public class SubmissionService implements ISubmissionService {
    @Autowired
    ISubmissionRepository submissionRepository;
    @Autowired
    IMilestoneRepository milestoneRepository;
    @Autowired
    IProjectRepository projectRepository;

    @Override
    public Submission insertSubmission(Integer milestoneId, String note, User user, Integer projectId) {
        Milestone milestone = milestoneRepository.findMilestoneById(milestoneId);
        Submission submission = new Submission();

        submission.setMilestone(milestone);
        submission.setProject(milestone.getProject());
        submission.setNote(note);
        submission.setCreateBy(user);
        submission.setProject(projectRepository.findById(projectId).get());

        submissionRepository.save(submission);
        return submission;
    }

    @Override
    public boolean updateFileLocation(Integer submissionId, String fileLocation) {

        try {
            Submission submission = submissionRepository.findById(submissionId).get();

            submission.setFileLocation(fileLocation);

            submissionRepository.save(submission);

            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

    }

    @Override
    public Submission findById(Integer submissionId) {
        return submissionRepository.findById(submissionId).orElseGet(null);
    }

    @Override
    public List<Submission> findAllByProjectId(Integer projectId) {
        return submissionRepository.findAllByProjectId(projectId);
    }

    @Override
    public boolean updateComment(Integer submissionId, String comment) {
        try {
            Submission submission = submissionRepository.findById(submissionId).get();

            submission.setComment(comment);

            submissionRepository.save(submission);

            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
