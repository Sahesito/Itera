package com.sahe.itera.presentation.screens.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.Grade
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.usecase.grade.*
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import com.sahe.itera.domain.usecase.subject.UpdateSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    private val getGrades: GetGradesUseCase,
    private val insertGrade: InsertGradeUseCase,
    private val updateGrade: UpdateGradeUseCase,
    private val deleteGrade: DeleteGradeUseCase,
    private val updateSubject: UpdateSubjectUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val overallAverage: StateFlow<Float?> = subjects.map { list ->
        val avgs = list.mapNotNull { it.currentAverage }
        if (avgs.isEmpty()) null else avgs.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _gradesBySubject = mutableMapOf<Long, StateFlow<List<Grade>>>()

    fun getGradesForSubject(subjectId: Long): StateFlow<List<Grade>> =
        _gradesBySubject.getOrPut(subjectId) {
            getGrades(subjectId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }

    fun insert(grade: Grade) {
        viewModelScope.launch {
            insertGrade(grade)
            recalculateAverage(grade.subjectId)
        }
    }

    fun update(grade: Grade) {
        viewModelScope.launch {
            updateGrade(grade)
            recalculateAverage(grade.subjectId)
        }
    }

    fun delete(grade: Grade) {
        viewModelScope.launch {
            deleteGrade(grade)
            recalculateAverage(grade.subjectId)
        }
    }

    private suspend fun recalculateAverage(subjectId: Long) {
        getGrades(subjectId).first().let { grades ->
            val rendered = grades.filter { it.score != null }
            val totalWeight = grades.sumOf { it.weight.toDouble() }.toFloat()
            val avg = if (rendered.isEmpty()) null else {
                rendered.sumOf { g ->
                    val normalized = (g.score!! / g.maxScore) * 20f
                    (normalized * g.weight / totalWeight).toDouble()
                }.toFloat()
            }
            val subject = subjects.value.firstOrNull { it.id == subjectId } ?: return
            updateSubject(subject.copy(currentAverage = avg))
        }
    }

    fun simulate(grades: List<Grade>, target: Float, subjectId: Long): Float? {
        val subject = subjects.value.firstOrNull { it.id == subjectId } ?: return null
        val totalWeight = grades.sumOf { it.weight.toDouble() }.toFloat()
        val renderedPoints = grades.filter { it.score != null }.sumOf { g ->
            val normalized = (g.score!! / g.maxScore) * 20f
            (normalized * g.weight / totalWeight).toDouble()
        }.toFloat()
        val pendingWeight = grades.filter { it.score == null }
            .sumOf { it.weight.toDouble() }.toFloat()
        if (pendingWeight <= 0f) return null
        return ((target - renderedPoints) * totalWeight / pendingWeight)
            .coerceIn(0f, 20f)
    }


    fun reconfigureGrades(subject: Subject, grades: List<Grade>, targetGrade: Float) {
        viewModelScope.launch {
            getGrades(subject.id).first().forEach { deleteGrade(it) }
            updateSubject(subject.copy(targetGrade = targetGrade, currentAverage = null))
            grades.forEach { insertGrade(it) }
        }
    }
}