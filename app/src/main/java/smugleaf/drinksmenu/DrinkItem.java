package smugleaf.drinksmenu;

public class DrinkItem {

    private String glassware = "";
    private String name = "";
    private String body = "";
    private String price = "";
    private String backgroundColor = "";
    private String glassColor = "";
    private String fontColor = "";

    public DrinkItem(String glassware, String name, String body, String price, String backgroundColor, String glassColor, String fontColor) {
        this.glassware = glassware;
        this.name = name;
        this.body = body;
        this.price = price;
        this.backgroundColor = backgroundColor;
        this.glassColor = glassColor;
        this.fontColor = fontColor;
    }

    public String getGlassware() {  return glassware; }
    public String getName() { return name; }
    public String getBody() { return body; }
    public String getPrice() { return price; }
    public String getBackgroundColor() { return backgroundColor; }
    public String getGlassColor() { return glassColor; }
    public String getFontColor() { return fontColor; }
}