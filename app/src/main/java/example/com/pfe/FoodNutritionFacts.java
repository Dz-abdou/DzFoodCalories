package example.com.pfe;

public class FoodNutritionFacts {

    private String name;
    private String calories;
    private String protein;
    private String carbohydrates;
    private String lipids;
    private String saturatedFat;
    private String unsaturatedFat;
    private String polyunsaturatedFat;
    private String MonounsaturatedFat;
    private String cholesterol;
    private String dietaryFiber;
    private String sugars;

    public FoodNutritionFacts() {

    }

    public FoodNutritionFacts(String name, String calories, String protein, String carbohydrates,
                              String lipids, String saturatedFat, String unsaturatedFat,
                              String polyunsaturatedFat, String monounsaturatedFat, String cholesterol,
                              String dietaryFiber, String sugars) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.lipids = lipids;
        this.saturatedFat = saturatedFat;
        this.unsaturatedFat = unsaturatedFat;
        this.polyunsaturatedFat = polyunsaturatedFat;
        MonounsaturatedFat = monounsaturatedFat;
        this.cholesterol = cholesterol;
        this.dietaryFiber = dietaryFiber;
        this.sugars = sugars;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getLipids() {
        return lipids;
    }

    public void setLipids(String lipids) {
        this.lipids = lipids;
    }

    public String getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(String saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public String getUnsaturatedFat() {
        return unsaturatedFat;
    }

    public void setUnsaturatedFat(String unsaturatedFat) {
        this.unsaturatedFat = unsaturatedFat;
    }

    public String getPolyunsaturatedFat() {
        return polyunsaturatedFat;
    }

    public void setPolyunsaturatedFat(String polyunsaturatedFat) {
        this.polyunsaturatedFat = polyunsaturatedFat;
    }

    public String getMonounsaturatedFat() {
        return MonounsaturatedFat;
    }

    public void setMonounsaturatedFat(String monounsaturatedFat) {
        MonounsaturatedFat = monounsaturatedFat;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(String dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public String getSugars() {
        return sugars;
    }

    public void setSugars(String sugars) {
        this.sugars = sugars;
    }
}
