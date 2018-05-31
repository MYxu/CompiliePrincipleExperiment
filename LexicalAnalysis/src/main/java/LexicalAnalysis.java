package main.java;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: MY.Xu
 * Date: 2018/5/29
 * Time: 21:02
 * Description:
 */
public class LexicalAnalysis {

    // Java关键字
    public static String keywords[] = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "case", "class", "const", "continue", "default", "do",
            "double", "else", "enum", "extends", "final", "finally", "continue",
            "float", "for", "goto", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new", "package","private",
            "protected","public", "return","short","static","strictfp","super",
            "synchronized","this", "throw","throws","transient","try","void",
            "volatile","while"
    };

    // 运算符
    public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~',
            '^', '!', '(', ')', '[', ']', '{', '}', '%', ';', ',', '#', '.' };

    // 界符
    public static char boundary[] = { ',', ';', '[', ']', '(', ')', '.', '{', '}'};

    // 字符串DFA,a代表任意字符，b代表除\和'之外的字符
    public static String stringDFA[] = {
            "#\\b#",
            "##a#",
            "#\\b\"",
            "####",
    };

    // 字符DFA,a代表任意字符，b代表除\和'之外的字符
    public static String charDFA[] = {
            "#\\b#",
            "##a#",
            "###\'",
            "####"
    };

    // 实数DFA
    public static String digitDFA[] = {
            "#d#####",
            "#d.#e##",
            "###d###",
            "###de##",
            "#####-d",
            "######d",
            "######d"
    };

    // 多行注释DFA
    public static String noteDFA[] = {
            "#####",
            "##*##",
            "##c*#",
            "##c*/",
            "#####"};

    // 存储Token子串的类别以及对应的种别码信息
    public TokenInfo tokenKeyword;           // Token为关键字
    public TokenInfo tokenOperator;          // Token为运算符
    public TokenInfo tokenBoundary;          // Token为界符
    public TokenInfo tokenIdentifier;        // Token为标识符
    public TokenInfo tokenAnnotate;          // Token为注释
    public TokenInfo tokenIntConstant;       // Token为整型常量
    public TokenInfo tokenFloatConstant;     // Token为浮点型常量
    public TokenInfo tokenCharConstant;      // Token为字符常量
    public TokenInfo tokenStringConstant;    // Token为字符串常量

    // 存储符号表信息
    public static int symbol_pos; // 记录符号表位置
    public static Map<String,Integer> symbol = new HashMap<>(); // 符号表HashMap

    // UI界面
    private String codeString;                  // 存储编辑区中输入的代码字符串
    private JTable jTableTokenInfo;             // 显示Token信息
    private JTable jTableErrorInfo;             // 显示错误信息
    private JTable jTableSymbolInfo;            // 显示符号表信息
    private DefaultTableModel tableModelToken;  // 用于添加Token信息到表格中
    private DefaultTableModel tableModelError;  // 用于添加Error信息到表格中
    private DefaultTableModel tableModelSymbol; // 用于添加symbol信息到表格中

    // 构造函数
    public LexicalAnalysis(String codeString,JTable jTableTokenInfo,JTable jTableErrorInfo,
                           JTable jTableSymbolInfo)
    {

        // 初始化Token类别、种别码信息
        this.tokenKeyword        = new TokenInfo("关键字","100");
        this.tokenOperator       = new TokenInfo("运算符","200");
        this.tokenBoundary       = new TokenInfo("界符","300");
        this.tokenIdentifier     = new TokenInfo("标识符","400");
        this.tokenAnnotate       = new TokenInfo("注释","500");
        this.tokenIntConstant    = new TokenInfo("整型常量","600");
        this.tokenFloatConstant  = new TokenInfo("浮点型常量","601");
        this.tokenCharConstant   = new TokenInfo("字符常量","602");
        this.tokenStringConstant = new TokenInfo("字符串常量","603");

        // 初始化UI界面
        this.codeString       = codeString;
        this.jTableTokenInfo  = jTableTokenInfo;
        this.jTableErrorInfo  = jTableErrorInfo;
        this.jTableSymbolInfo = jTableSymbolInfo;

        this.tableModelToken  = (DefaultTableModel) jTableTokenInfo.getModel();
        this.tableModelError  = (DefaultTableModel) jTableErrorInfo.getModel();
        this.tableModelSymbol = (DefaultTableModel) jTableSymbolInfo.getModel();

    }

    /*
     * @author MYXuu
     * @description : 判断某个字符是否为字母或者下划线
     * @date 2018/5/31 11:34
     *
     * @param [ch]  单个字符
     * @return java.lang.Boolean
     */
    public static Boolean isAlpha(char ch) {
        return (
                (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_' )
        );
    }

    /*
     * @author MYXuu
     * @description : 判断某个字符是否为数字
     * @date 2018/5/31 11:36
     *
     * @param [ch]  单个字符
     * @return java.lang.Boolean
     */
    public static Boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    /*
     * @author MYXuu
     * @description : 判断单个字符是否为单运算符
     * @date 2018/5/31 12:58
     *
     * @param [ch] 单个字符
     * @return java.lang.Boolean
     */
    public static Boolean isOperator(char ch) {
        for (char anOperator : operator) {
            if (ch == anOperator)
                return true;
        }
        return false;
    }

    /*
     * @author MYXuu
     * @description : 判断某个单词是否为语言关键字
     * @date 2018/5/31 13:03
     *
     * @param [word]  单词
     * @return java.lang.Boolean
     */
    public static Boolean isKeyword(String word) {
        for (String keyword : keywords) {
            if (word.equals(keyword))
                return true;
        }
        return false;
    }


    /*
     * @author MYXuu
     * @description : 判断某个字符是否可组合运算符'='形成新的运算符，如+=、-=等
     * @date 2018/5/31 13:14
     *
     * @param [ch]  单个字符
     * @return java.lang.Boolean
     */
    public static Boolean isPlusEqu(char ch) {

        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%'
                || ch == '=' || ch == '>' || ch == '<' || ch == '&' || ch == '|'
                || ch == '^' || ch == '!' ;
    }

    /*
     * @author MYXuu
     * @description : 判断运算符可否组合自身形成新运算符，如 ++、--、||、&&等
     * @date 2018/5/31 13:24
     *
     * @param [ch] 单个字符
     * @return java.lang.Boolean
     */
    public static Boolean isPlusSelf(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '>' || ch == '<';
    }


    public static Boolean isEsSt(char ch) {
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't'
                || ch == 'v' || ch == '0' || ch == '\\' || ch == '\'' || ch == '\"';
    }





    /*
     * @author MYXuu
     * @description : 词法分析
     * @date 2018/5/31 11:29
     *
     * @param []
     * @return void
     */
    public void LexAnalysis() {

        symbol.clear(); // 清空符号表
        symbol_pos = 0; // 符号表起始位置设置为0

        // 将编辑区中用户输入的程序代码按行进行分解
        String[] codeLines = codeString.split("\n");

        // 依次对每行代码进行单词切割、词法分析
        for (int i = 0; i < codeLines.length; i++) {
            // 取出一行代码字符串
            String strLine = codeLines[i];

            if (!strLine.equals("")) {
                // 对当前行的代码字符串进行切割，切割成单个字符存储的字符数组
                char[] charArray = strLine.toCharArray();

                // 遍历charArray中每个字符，按照以下规则切割成不同类型的单词Token
                for (int j = 0; j < charArray.length ; j++)
                {
                    char ch = charArray[j];
                    String token = "";  // 用于记录切割出来的Token

                    // 1.关键字、标识符的切割识别
                    if (isAlpha(ch))
                    {
                        do {
                            token += ch;
                            j++;
                            if (j >= charArray.length)
                                break;

                            ch = charArray[j];
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));

                        // 由于指针加1，需要指针回退
                        j--;

                        // 切割出来的是关键字
                        if (isKeyword(token))
                        {
                            tableModelToken.addRow(new Object[]{token,tokenKeyword.category,
                                    tokenKeyword.categoryCode, i + 1});
                            jTableTokenInfo.invalidate();
                        }

                        // 切割出来的token是标识符
                        if (!isKeyword(token))
                        {
                            tableModelToken.addRow(new Object[]{token,tokenIdentifier.category,
                                    tokenIdentifier.categoryCode,i + 1});
                            jTableTokenInfo.invalidate();

                            // 如果符号表为空或者符号表不包含当前的token则需要加入符号表中
                            if (symbol.isEmpty() ||
                                    (!symbol.isEmpty() && !symbol.containsKey(token))) {
                                tableModelSymbol.addRow(new Object[]{token,symbol_pos});
                                symbol_pos ++ ;
                                jTableSymbolInfo.invalidate();
                            }
                        }
                        token = ""; // 当前的token切割识别完毕之后需要重置
                    }

                    // 2.数字常量的切割识别
                    if (isDigit(ch))
                    {

                    }

                }

            }
        }


    }
}
