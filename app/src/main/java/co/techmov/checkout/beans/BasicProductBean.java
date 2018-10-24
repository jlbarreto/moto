package co.techmov.checkout.beans;

public class BasicProductBean {
	private String detail;
    private String comments;
    private String ingredients;
    private String conditions;

    public BasicProductBean(String detail, String comments) {
        this.detail = detail;
        this.comments = comments;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
