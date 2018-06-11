package main;


/**
 * Created by IntelliJ IDEA.
 * User: MY.Xu
 * Date: 2018/5/30
 * Time: 0:11
 * Description: Token子串的类别以及对应的种别码
 */
public class TokenInfo {
    public String category;         // 类别
    public String categoryCode;     // 种别码


    public TokenInfo(String category,String categoryCode) {
        this.category = category;
        this.categoryCode = categoryCode;
    }

}
