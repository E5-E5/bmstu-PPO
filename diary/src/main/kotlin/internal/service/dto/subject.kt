package internal.service.dto

import model.Count

data class CreateSubjectRequest(
    var SubjectId: Int,
    var Name: String,
    var CountTeachers: Count
)

data class GetSubjectRequestById(
    var SubjectId: Int
)

data class GetSubjectsForClassRequest(
    var ClassId: Int
)

data class GetSubjectRequestByName(
    var Name: String
)

data class DeleteSubjectRequest(
    var SubjectId: Int
)