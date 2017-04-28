package com.yourong.core.sys.model;

public class SysLogWithBLOBs extends SysLog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 294962594822391216L;

	/**操作提交的数据**/
    private String params;

    /**异常信息**/
    private String exception;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception == null ? null : exception.trim();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((exception == null) ? 0 : exception.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysLogWithBLOBs other = (SysLogWithBLOBs) obj;
		if (exception == null) {
			if (other.exception != null)
				return false;
		} else if (!exception.equals(other.exception))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysLogWithBLOBs [params=");
		builder.append(params);
		builder.append(", exception=");
		builder.append(exception);
		builder.append("]");
		return builder.toString();
	}
}