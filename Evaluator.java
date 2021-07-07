/**
 *
 * @author Ahmed
 */
public class Evaluator {

    public static String errMsg = "";

    public static int[] parseDate(String s) {
        int s1 = s.indexOf('-');
        int s2 = s.indexOf('-', s1 + 1);
        if (s1 == -1 || s2 == -1 || s1 == s2) {
            return new int[]{0, 0, 0};
        }
        String dd = s.substring(0, s1);
        String mm = s.substring(s1 + 1, s2);
        String yyyy = s.substring(s2 + 1);
        int DD, MM, YYYY;
        DD = Integer.parseInt(dd);
        MM = Integer.parseInt(mm);
        YYYY = Integer.parseInt(yyyy);
        return new int[]{DD, MM, YYYY};
    }

    public static boolean verifyDate(String s,int selected) {
        int DD, MM, YYYY;
        try {
            int date[] = Evaluator.parseDate(s);
            DD = date[0];
            MM = date[1];
            YYYY = date[2];
            
            if (YYYY > 2099 || YYYY < 1900) {
                errMsg = L.Year_range[L.selected]+" 1900-2099";
                return false;
            }
            if(selected==1){//ethiopic date
                if(MM>13 || DD> 30||(MM==13 && DD>6 && isLeapYear(YYYY+1))||(MM==13 && DD>5 && !isLeapYear(YYYY+1))){
                    errMsg= L.Invalid_Date[L.selected];
                    return false;
                }
                else return true;
            }
            if (MM == 2 && (isLeapYear(YYYY) && DD > 29 || !isLeapYear(YYYY) && DD > 28)) {
                errMsg = L.Invalid_Date[L.selected];
                return false;
            }
            if (DD > 31 || DD < 1) {
                errMsg = L.Invalid_Date[L.selected];
                return false;
            }
            if (DD == 31 && (MM == 4 || MM == 6 || MM == 9 || MM == 11)) {
                errMsg = L.Invalid_Date[L.selected];
                return false;
            }
            if (MM > 12 || MM < 1) {
                errMsg = L.Invalid_Date[L.selected];
                return false;
            }

        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

    private static boolean isLeapYear(int year) {
        if (year % 4 == 0 && year % 100 != 1900 || year % 400 == 0) {
            return true;
        }
        return false;
    }

    static int[] parseTime(String s) {
        int s1 = s.indexOf(':');
        String hh=s.substring(0, s1);
        String mm=s.substring(s1+1);
        return new int[]{Integer.parseInt(hh),Integer.parseInt(mm)};
    }

    static boolean verifyTime(String s) {
        try {
            int time[] = parseTime(s);
            if (time[0] > 12 || time[0] < 1 || time[1] > 59 || time[1] < 0) {
                errMsg = L.Invalid_Time[L.selected];
                return false;
            }
        } catch (Exception e) {
            errMsg = L.Invalid_Time[L.selected];
                return false;
        }
        return true;
    }
}
