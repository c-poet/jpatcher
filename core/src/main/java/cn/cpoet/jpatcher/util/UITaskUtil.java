package cn.cpoet.jpatcher.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author CPoet
 */
public abstract class UITaskUtil {
    private UITaskUtil() {
    }

    public static boolean isUi() {
        return ApplicationManager.getApplication().isDispatchThread();
    }

    public static void runUI(Runnable runnable) {
        runUI(runnable, ModalityState.defaultModalityState());
    }

    public static void runUI(Runnable runnable, ModalityState state) {
        if (isUi()) {
            runnable.run();
        } else {
            ApplicationManager.getApplication().invokeLater(runnable, state);
        }
    }

    public static void runNotUi(Runnable runnable) {
        if (isUi()) {
            CompletableFuture.runAsync(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * 执行后台任务，不可取消
     *
     * @param project  项目
     * @param title    任务标题
     * @param runnable 执行的任务
     */
    public static void runProgress(Project project, String title, Consumer<ProgressIndicator> runnable) {
        runProgress(project, title, false, runnable);
    }

    /**
     * 执行后台任务
     *
     * @param project        项目
     * @param title          任务标题
     * @param canBeCancelled 是否支持取消
     * @param runnable       执行的任务
     */
    public static void runProgress(Project project, String title, boolean canBeCancelled, Consumer<ProgressIndicator> runnable) {
        new Task.Backgroundable(project, title, canBeCancelled) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                runnable.accept(progressIndicator);
            }
        }.queue();
    }
}
