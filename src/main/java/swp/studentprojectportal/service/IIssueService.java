package swp.studentprojectportal.service;

import swp.studentprojectportal.model.Issue;

import java.util.List;

public interface IIssueService {

    Issue getIssueById(Integer issueId);

    List<Issue> getAllIssueByStudentId(Integer studentId);

    public List<Issue> findAllByMilestoneId(Integer milestoneId);

    public boolean updateIssueAssignee(Integer issueId, Integer assigneeId);
}
