package org.eztask;

import org.eztask.entity.Task;

import java.util.List;

public class TaskPrinter {
    public void printTasks(List<Task> taskList) {
        for (Task task : taskList) {
            System.out.println(task.toString());
        }
    }
}
