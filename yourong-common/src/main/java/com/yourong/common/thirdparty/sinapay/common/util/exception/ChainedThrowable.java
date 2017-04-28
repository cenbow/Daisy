package com.yourong.common.thirdparty.sinapay.common.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>
 * 实现此接口的异常, 是由另一个异常引起的.
 * </p>
 * @author sean won
 * @version $Id: ChainedThrowable.java, v 0.1 2009-9-18 上午09:50:45 fuyangbiao Exp $
 */
public interface ChainedThrowable {
    /**
     * 取得异常的起因.
     *
     * @return 异常的起因.
     */
    Throwable getCause();

    /**
     * 打印调用栈到标准错误.
     */
    void printStackTrace();

    /**
     * 打印调用栈到指定输出流.
     *
     * @param stream 输出字节流.
     */
    void printStackTrace(PrintStream stream);

    /**
     * 打印调用栈到指定输出流.
     *
     * @param writer 输出字符流.
     */
    void printStackTrace(PrintWriter writer);

    /**
     * 打印异常的调用栈, 不包括起因异常的信息.
     *
     * @param writer 打印到输出流
     */
    void printCurrentStackTrace(PrintWriter writer);
}
