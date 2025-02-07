package internal.storage

import internal.service.dto.*
import model.Assessment

data class get_assessment(val res_Assessment: List<Assessment>, val error: Int)

interface AssessmentStorage {
    fun CreateAssessment(request: CreateAssessmentRequest): Int
    fun DeleteAssessment(request: DeleteAssessmentRequest): Int
    fun GetStudentAssessments(request: GetAssessmentsRequest): get_assessment
    fun UpdateAssessment(request: UpdateAssessmentRequest): Int
}
