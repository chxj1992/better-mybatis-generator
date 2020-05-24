package name.chxj.mybatis.generator.util;

import org.mybatis.generator.api.ProgressCallback;

/**
 * mybatis generator进度回调
 *
 * @author kangtian
 * @date 2018/7/17
 */
public class GeneratorCallback implements ProgressCallback {
    @Override
    public void introspectionStarted(int i) {
    }

    @Override
    public void generationStarted(int i) {

    }

    @Override
    public void saveStarted(int i) {

    }

    @Override
    public void startTask(String s) {
        System.out.println("startTask" + s);
    }

    @Override
    public void done() {
        System.out.println("代码生成完毕!");
    }

    @Override
    public void checkCancel() throws InterruptedException {

    }
}
