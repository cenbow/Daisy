package com.yourong.core.mc.model;

import java.io.Serializable;

public class HelpCenterQuestionShow implements Serializable {
	
	private static final long serialVersionUID = -7454452347584163767L;
	
	private Long id;

	/** 问题序号排序 **/
	private Integer sortOfQues;
	
	/** 标签序号排序 **/
	private Integer sortOfLabel;
	
	/** 标签类别**/
	private String category;
	
	/** 标签名称**/
	private String labelName;
	
	/** 问题序号**/
	private String questionOrder;
	
	/** 问题内容 **/
	private String content;
	
	/** 问题答案 **/
	private String answer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSortOfQues() {
		return sortOfQues;
	}

	public void setSortOfQues(Integer sortOfQues) {
		this.sortOfQues = sortOfQues;
	}

	public Integer getSortOfLabel() {
		return sortOfLabel;
	}

	public void setSortOfLabel(Integer sortOfLabel) {
		this.sortOfLabel = sortOfLabel;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(String questionOrder) {
		this.questionOrder = questionOrder;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((labelName == null) ? 0 : labelName.hashCode());
		result = prime * result
				+ ((questionOrder == null) ? 0 : questionOrder.hashCode());
		result = prime * result
				+ ((sortOfLabel == null) ? 0 : sortOfLabel.hashCode());
		result = prime * result
				+ ((sortOfQues == null) ? 0 : sortOfQues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HelpCenterQuestionShow other = (HelpCenterQuestionShow) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (labelName == null) {
			if (other.labelName != null)
				return false;
		} else if (!labelName.equals(other.labelName))
			return false;
		if (questionOrder == null) {
			if (other.questionOrder != null)
				return false;
		} else if (!questionOrder.equals(other.questionOrder))
			return false;
		if (sortOfLabel == null) {
			if (other.sortOfLabel != null)
				return false;
		} else if (!sortOfLabel.equals(other.sortOfLabel))
			return false;
		if (sortOfQues == null) {
			if (other.sortOfQues != null)
				return false;
		} else if (!sortOfQues.equals(other.sortOfQues))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HelpCenterQuestionShow [id=" + id + ", sortOfQues="
				+ sortOfQues + ", sortOfLabel=" + sortOfLabel + ", category="
				+ category + ", labelName=" + labelName + ", questionOrder="
				+ questionOrder + ", content=" + content + ", answer=" + answer
				+ "]";
	}


}