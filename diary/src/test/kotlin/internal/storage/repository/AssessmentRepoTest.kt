package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import internal.storage.AssessmentStorage
import logger.Logger
import model.Count
import model.Gender
import model.StudentAssessment
import org.example.internal.storage.repository.AssessmentRepo
import org.example.internal.storage.repository.StudentRepo
import org.example.internal.storage.repository.SubjectRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date

class AssessmentRepoTest {

    @Test
    fun createAssessment() {
        var request_create_1 = CreateAssessmentRequest(7, 1, 8, StudentAssessment.TWO, Date.valueOf("2023-11-13"))
        var sut = AssessmentRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateAssessment(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

//    @Test
//    fun deleteAssessment() {
//        var request_delete_1 = DeleteAssessmentRequest(1, 1, 2, Date.valueOf("2023-11-13"))
//        var sut = AssessmentRepo(Logger(), PostgresDBConnector())
//        var actual = sut.DeleteAssessment(request_delete_1)
//        var expected = 0
//        assertEquals(expected, actual)
//    }

    @Test
    fun getStudentAssessments() {
        var request_get_1 = GetAssessmentsRequest(1, 1, Date.valueOf("2023-10-13"), Date.valueOf("2023-12-13"))
        var sut = AssessmentRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetStudentAssessments(request_get_1)
        println(actual.res_Assessment)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun updateAssessment() {
        var request_update_1 = UpdateAssessmentRequest(1, 1, 2, StudentAssessment.FIVE, Date.valueOf("2023-11-13"))
        var sut = AssessmentRepo(Logger(), PostgresDBConnector())
        var actual = sut.UpdateAssessment(request_update_1)
        var expected = 0
        assertEquals(expected, actual)
    }
}