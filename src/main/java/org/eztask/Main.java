package org.eztask;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.entity.TaskManager;
import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.eztask.search.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("------TaskManager-------------");
        System.out.println("-------START---------");
        User user1= new User("dummy1");
        User user2= new User("dummy2");
        User user3= new User("dummy3");
        User user4= new User("dummy4");

        TaskManager taskManager = TaskManager.getInstance();
        taskManager.createTask("Task1", "Description1", user1);
        taskManager.createTask("Task2", "Description2", user2);
        taskManager.createTask("Task3", "Description3", user3);
        taskManager.createTask("Task4", "Description4", user4);

        TaskSearcher taskSearcher = new TaskSearcher(new TaskCreaterSearchStrategy());
        List<Task> result = taskSearcher.search(taskManager.getTaskList(), user2);
        TaskPrinter taskPrinter = new TaskPrinter();
        taskPrinter.printTasks(result);
        for (Task task: taskManager.getTaskList()) {
            taskManager.updateTaskStatus(task, TaskStatus.NOT_PICKED);
            taskManager.updateTaskPriority(task, TaskPriority.MODERATE);
            System.out.println(task.getCreater().getName());
            if(task.getCreater().getName().equals(user4.getName())) {
                taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                taskManager.updateTaskPriority(task, TaskPriority.HIGH);
            }
        }

        System.out.println("---------DEV_IN_PROGRESS-------");
        taskSearcher = new TaskSearcher(new TaskStatusSearchStrategy());
        result = taskSearcher.search(taskManager.getTaskList(), TaskStatus.DEV_IN_PROGRESS);
        taskPrinter.printTasks(result);
        System.out.println("---------END_DEV_IN_PROGRESS-------");
        taskSearcher = new TaskSearcher(new TaskPrioritySearchStrategy());
        System.out.println("---------HIGH_PRIORITY-------");
        result = taskSearcher.search(taskManager.getTaskList(), TaskPriority.HIGH);
        taskPrinter.printTasks(result);
        System.out.println("---------END_HIGH_PRIORITY-------");
        taskPrinter.printTasks(taskManager.getTaskList());



        System.out.println("------END----------------");
    }
}