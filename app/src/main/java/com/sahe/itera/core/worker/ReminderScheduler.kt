package com.sahe.itera.core.worker

import android.content.Context
import androidx.work.*
import com.sahe.itera.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(task: Task) {
        val dueDateTime = task.dueDateTime ?: return
        val reminderTime = dueDateTime.minusMinutes(30)
        val now = LocalDateTime.now()
        if (reminderTime.isBefore(now)) return

        val delay = reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                System.currentTimeMillis()

        val data = workDataOf(
            TaskReminderWorker.KEY_TASK_TITLE to task.title,
            TaskReminderWorker.KEY_TASK_ID to task.id.toInt()
        )

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("task_${task.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_${task.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$taskId")
    }
}