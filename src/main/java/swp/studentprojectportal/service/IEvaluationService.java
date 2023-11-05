package swp.studentprojectportal.service;

import swp.studentprojectportal.model.Evaluation;

import java.util.List;

public interface IEvaluationService {
    List<Evaluation> getEvaluationBySubmissionId(int submissionId);
}