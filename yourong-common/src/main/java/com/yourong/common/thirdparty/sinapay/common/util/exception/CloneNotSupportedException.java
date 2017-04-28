package com.yourong.common.thirdparty.sinapay.common.util.exception;

/**
 * <p>不支持克隆异常</p>
 * @author sean won
 * @version $Id: CloneNotSupportedException.java, v 0.1 2009-9-18 上午09:58:04 fuyangbiao Exp $
 */
public class CloneNotSupportedException extends ChainedRuntimeException {

    private static final long serialVersionUID = 4420983948686625581L;

    /**
     * 构造一个空的异常.
     */
    public CloneNotSupportedException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     *
     * @param message 详细信息
     */
    public CloneNotSupportedException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     *
     * @param cause 异常的起因
     */
    public CloneNotSupportedException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     *
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public CloneNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
