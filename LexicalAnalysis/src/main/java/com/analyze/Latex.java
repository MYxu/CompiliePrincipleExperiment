package main.java.com.analyze;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class Latex {
    public static int symbol_pos = 0;//记录符号表位置
    public static Map<String, Integer> symbol = new HashMap<>();//符号表HashMap
    //关键字
    public static String keywords[] = {"auto", "double", "int", "struct",
            "break", "else", "long", "switch", "case", "enum", "register",
            "typedef", "char", "extern", "return", "union", "const", "float",
            "short", "unsigned", "continue", "for", "signed", "void",
            "default", "goto", "sizeof", "volatile", "do", "if", "while",
            "static", "main", "String"};
    public static char operator[] = {'+', '-', '*', '=', '<', '>', '&', '|', '~',
            '^', '!', '(', ')', '[', ']', '{', '}', '%', ';', ',', '#', '.'};
    public static char boundary[] = {',', ';', '[', ']', '(', ')', '.', '{', '}'};

    //a代表任意字符，b代表除\和'之外的字符
    public static String stringDFA[] = {
            "#\\b#",
            "##a#",
            "#\\b\"",
            "####"
    };

    //a代表任意字符，b代表除\和'之外的字符
    public static String charDFA[] = {
            "#\\b#",
            "##a#",
            "###\'",
            "####"
    };

    //DFA of digit
    public static String digitDFA[] = {
            "#d#####",
            "#d.#e##",
            "###d###",
            "###de##",
            "#####-d",
            "######d",
            "######d"};
    //多行注释DFA
    public static String noteDFA[] = {
            "#####",
            "##*##",
            "##c*#",
            "##c*/",
            "#####"};

    private String text;
    private JTable jTable1;
    private JTable jTable2;
    private JTable jTable3;

    public Latex(String text, JTable jTable1, JTable jTable2, JTable jTable3) {
        this.text = text;
        this.jTable1 = jTable1;
        this.jTable2 = jTable2;
        this.jTable3 = jTable3;
    }

    /*
     * @author MYXuu
     * @description : 判断单个字符是否为字母或者下划线
     * @date 2018/5/30 20:36
     *
     * @param [ch]
     * @return java.lang.Boolean
     */
    public static Boolean isAlpha(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
    }

    /*
     * @author MYXuu
     * @description : 判断单个字符是否为数字
     * @date 2018/5/30 20:36
     *
     * @param [ch]
     * @return java.lang.Boolean
     */
    public static Boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    /*
     * @author MYXuu
     * @description : 判断单个字符是否为运算符
     * @date 2018/5/30 20:37
     *
     * @param [ch]
     * @return java.lang.Boolean
     */
    public static Boolean isOp(char ch) {
        for (char anOperator : operator)
            if (ch == anOperator) {
                return true;
            }
        return false;
    }

    /*
     * @author MYXuu
     * @description : 判断字符串是否为关键字
     * @date 2018/5/30 20:38
     *
     * @param [str]
     * @return java.lang.Boolean
     */
    public static Boolean isMatchKeyword(String str) {
        Boolean flag = false;
        for (String keyword : keywords) {
            if (str.equals(keyword)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /*
     * @author MYXuu
     * @description : 判断运算符可否与等号运算符组合，例如+=、-=等
     * @date 2018/5/30 20:40
     *
     * @param [ch]
     * @return java.lang.Boolean
     */
    public static Boolean isPlusEqu(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '='
                || ch == '>' || ch == '<' || ch == '&' || ch == '|'
                || ch == '^';
    }

    /*
     * @author MYXuu
     * @description : 判断运算符可否组合自身形成新运算符，如 ++、--、||、&&
     * @date 2018/5/30 19:00
     *
     * @param [ch]
     * @return java.lang.Boolean
     */
    public static Boolean isPlusSame(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|';
    }

    public static Boolean in_stringDFA(char ch, char key) {
        if (key == 'a')
            return true;
        if (key == '\\')
            return ch == key;
        if (key == '"')
            return ch == key;
        if (key == 'b')
            return ch != '\\' && ch != '"';
        return false;
    }

    public static Boolean isEsSt(char ch) {
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';
    }

    public static Boolean in_charDFA(char ch, char key) {
        if (key == 'a')
            return true;
        if (key == '\\')
            return ch == key;
        if (key == '\'')
            return ch == key;
        if (key == 'b')
            return ch != '\\' && ch != '\'';
        return false;
    }

    //判断输入符号是否符合状态机
    public static int in_digitDFA(char ch, char test) {
        if (test == 'd') {
            if (isDigit(ch))
                return 1;
            else
                return 0;
        } else {
            if (ch == test)
                return 1;
            else
                return 0;
        }
    }

    public static Boolean in_noteDFA(char ch, char nD, int s) {
        if (s == 2) {
            if (nD == 'c') {
                if (ch != '*')
                    return true;
                else
                    return false;
            }
        }
        if (s == 3) {
            if (nD == 'c') {
                if (ch != '*' && ch != '/')
                    return true;
                else
                    return false;
            }
        }
        return ch == nD;
    }

    public void analyze() {
        // 将用户输入的程序代码按行进行分解，即在程序的换行符处进行切割
        String[] texts = text.split("\n");
        symbol.clear();
        symbol_pos = 0;

        // 以行对代码字符串进行词法分析
        for (int m = 0; m < texts.length; m++)
        {
            String str = texts[m];

            if (str.equals("")) {
                continue;
            } else {
                // 将程序当前行的代码字符串转化为字符串数组
                char[] strLine = str.toCharArray();

                for (int i = 0; i < strLine.length; i++) {
                    //遍历strLine中的每个字符
                    char ch = strLine[i];

                    // 初始化token字符串为空
                    String token = "";

                    // 1.判断是否为标识符或者关键字
                    if (isAlpha(ch)) { //判断该字符是否为字母或者下划线

                        // 对当前行的代码字符串进行单词(Token)切割，即以空格进行切割
                        // 同时这是在识别标识符跟关键字，因此要满足当前字符为字母、数字、下划线
                        do {
                            token += ch;
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));

                        --i; //由于指针加1,指针回退

                        // 切割出来的Token是关键字
                        if (isMatchKeyword(token)) {
                            DefaultTableModel tableModel = (DefaultTableModel)
                                    jTable1.getModel();
                            tableModel.addRow(new Object[]{token, "关键字", "100", m + 1});
                            jTable1.invalidate();

                        } else { // 切割出来的Token是标识符
                            //如果符号表为空或符号表中不包含当前token，则加入
                            if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) {
                                symbol.put(token, symbol_pos);
                                DefaultTableModel tableModel3 = (DefaultTableModel) jTable3.getModel();
                                tableModel3.addRow(new Object[]{token, symbol_pos});
                                jTable3.invalidate();
                                symbol_pos++;
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                            tableModel1.addRow(new Object[]{token, "标识符", "200", m + 1});
                            jTable1.invalidate();
                        }
                        token = "";

                    } else if (isDigit(ch)) { // 2.判断数字常量
                        //初始化进入1状态
                        int state = 1;
                        //声明计数变量
                        int k;
                        Boolean isFloat = false;
                        while ((ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-'))
                        {
                            if (ch == '.' || ch == 'e')
                                isFloat = true;

                            for (k = 0; k <= 6; k++) {
                                char tmpStr[] = digitDFA[state].toCharArray();
                                if (ch != '#' && 1 == in_digitDFA(ch, tmpStr[k])) {
                                    token += ch;
                                    state = k;
                                    break;
                                }
                            }
                            if (k > 6) break;
                            //遍历符号先前移动
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                        }
                        Boolean haveMistake = false;

                        if (state == 2 || state == 4 || state == 5) {
                            haveMistake = true;
                        } else { // 1,3,6
                            // ch != ' ' 条件用于 int a = 123 ; 常量与分号有空格时不被识别为无效常量
                            // 因为ch = '' 时会导致!isDigit(ch)返回false
                            if ((!isOp(ch) || ch == '.') && !isDigit(ch) && ch != ' ')
                                haveMistake = true;
                        }

                        //错误处理
                        if (haveMistake) {
                            //一直到"可分割"的字符结束
                            while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') {
                                token += ch;
                                i++;
                                if (i >= strLine.length) break;
                                ch = strLine[i];
                            }
                            DefaultTableModel tableModel2 = (DefaultTableModel) jTable2.getModel();
                            tableModel2.addRow(new Object[]{m + 1, token + " 确认无符号常数输入正确"});
                            jTable2.invalidate();
                        } else {
                            if (isFloat) {
                                DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                                tableModel1.addRow(new Object[]{token, "浮点型常量", "300", m + 1});
                                jTable1.invalidate();
                            } else {
                                DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                                tableModel1.addRow(new Object[]{token, "整型常量", "301", m + 1});
                                jTable1.invalidate();
                            }
                        }
                        i--;
                        token = "";
                        //判断数字常量
                    }
                    //识别字符常量
                    else if (ch == '\'') {
                        //初始化状态为0
                        int state = 0;
                        //token加上’
                        token += ch;

                        while (state != 3) {
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                            for (int k = 0; k < 4; k++) {
                                char tmpStr[] = charDFA[state].toCharArray();
                                if (in_charDFA(ch, tmpStr[k])) {
                                    token += ch;
                                    state = k;
                                    break;
                                }
                            }
                        }
                        if (state != 3) {
                            DefaultTableModel tableModel2 = (DefaultTableModel) jTable2.getModel();
                            tableModel2.addRow(new Object[]{m + 1, token + " 字符常量引号未封闭"});
                            jTable2.invalidate();
                            i--;
                        } else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                            tableModel1.addRow(new Object[]{token, "字符常量", "302", m + 1});
                            jTable1.invalidate();
                        }
                        token = "";
                        //识别字符常量
                    }
                    //识别字符串常量
                    else if (ch == '"') {
                        String string = "";
                        string += ch;

                        int state = 0;
                        Boolean haveMistake = false;

                        while (state != 3) {
                            i++;

                            if (i >= strLine.length - 1) {
                                haveMistake = true;
                                break;
                            }

                            ch = strLine[i];

                            if (ch == '\0') {
                                haveMistake = true;
                                break;
                            }

                            for (int k = 0; k < 4; k++) {
                                char tmpStr[] = stringDFA[state].toCharArray();
                                if (in_stringDFA(ch, tmpStr[k])) {
                                    string += ch;
                                    if (k == 2 && state == 1) {
                                        if (isEsSt(ch)) //是转义字符
                                            token = token + '\\' + ch;
                                        else
                                            token += ch;
                                    } else if (k != 3 && k != 1)
                                        token += ch;
                                    state = k;
                                    break;
                                }
                            }
                        }
                        if (haveMistake) {
                            DefaultTableModel tableModel2 = (DefaultTableModel) jTable2.getModel();
                            tableModel2.addRow(new Object[]{m + 1, string + " 字符串常量引号未封闭"});
                            jTable2.invalidate();
                            --i;
                        } else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                            tableModel1.addRow(new Object[]{token, "字符串常量", "303", m + 1});
                            jTable1.invalidate();
                        }
                        token = "";
                        //识别字符串常量
                    }
                    // 3.运算符和界符
                    else if (isOp(ch)) {
                        token += ch;
                        //后面可以用一个"="
                        if (isPlusEqu(ch)) {
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                            if (ch == '=')
                                token += ch;
                            else {
                                //后面可以用一个和自己一样的
                                if (isPlusSame(strLine[i - 1]) && ch == strLine[i - 1])
                                    token += ch;
                                else
                                    --i;
                            }
                        }
                        //判断是否为界符
                        if (token.length() == 1) {
                            char signal = token.charAt(0);
                            boolean isbound = false;
                            for (int bound = 0; bound < boundary.length; bound++) {
                                if (signal == boundary[bound]) {
                                    DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                                    tableModel1.addRow(new Object[]{token, "界符", "304", m + 1});
                                    jTable1.invalidate();
                                    isbound = true;
                                    break;
                                }
                            }
                            if (!isbound) {
                                DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                                tableModel1.addRow(new Object[]{token, "运算符", "305", m + 1});
                                jTable1.invalidate();
                            }
                        } else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                            tableModel1.addRow(new Object[]{token, "运算符", "305", m + 1});
                            jTable1.invalidate();
                        }

                        token = "";
                        //识别运算符
                    }
                    // 4.识别注释//
                    else if (ch == '/') {
                        token += ch;
                        i++;
                        if (i >= strLine.length) break;
                        ch = strLine[i];

                        //不是多行注释及单行注释
                        if (ch != '*' && ch != '/') {
                            if (ch == '=')
                                token += ch; // /=
                            else {
                                --i; // 指针回退 // /
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                            tableModel1.addRow(new Object[]{token, "运算符", "305", m + 1});
                            jTable1.invalidate();
                            token = "";
                        } else {
                            // 注释可能是'//'也可能是'/*'
                            Boolean haveMistake = false;
                            int State = 0;
                            if (ch == '*') {
                                // ch == '*'
                                token += ch;
                                int state = 2;

                                while (state != 4) {
                                    i++;
                                    if (i >= strLine.length) break;
                                    ch = strLine[i];

                                    if (ch == '\0') {
                                        haveMistake = true;
                                        break;
                                    }
                                    for (int k = 2; k <= 4; k++) {
                                        char tmpStr[] = noteDFA[state].toCharArray();
                                        if (in_noteDFA(ch, tmpStr[k], state)) {
                                            token += ch;
                                            state = k;
                                            break;
                                        }
                                    }
                                }
                                State = state;
                                //if '*'
                            } else if (ch == '/') {
                                //单行注释读取所有字符
                                int index = str.lastIndexOf("//");
                                String tmpStr = str.substring(index);

                                int tmpint = tmpStr.length();
                                for (int k = 0; k < tmpint; k++)
                                    i++;
                                token = tmpStr;
                                State = 4;
                            }

                            if (haveMistake || State != 4) {
                                DefaultTableModel tableModel2 = (DefaultTableModel) jTable2.getModel();
                                tableModel2.addRow(new Object[]{m + 1, "注释未封闭"});
                                jTable2.invalidate();
                                --i;
                            } else {
                                DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();
                                tableModel1.addRow(new Object[]{token, "注释", "306", m + 1});
                                jTable1.invalidate();
                            }
                            token = "";
                            //为注释
                        }
                        //识别注释
                    }
                    //不合法字符
                    else {
                        if (ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                            DefaultTableModel tableModel2 = (DefaultTableModel) jTable2.getModel();
                            tableModel2.addRow(new Object[]{m + 1, "存在不合法字符"});
                            jTable2.invalidate();
                            System.out.println(ch);
                        }
                    }
                    //遍历strLine中的每个字符
                }
                //该行文本不为空
            }
            //遍历每行文本
        }
        //analyze()
    }
}
