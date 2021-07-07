
import java.io.IOException;
import java.util.Calendar;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Ahmed
 */
public class Ccanvas extends Canvas {

    //<editor-fold defaultstate="collapsed" desc="variables">
    Image bar_en, bar_et, calendar_en, calendar_et, month_en, month_et;
    static Image year_en, year_et, title, bg, selector, current;
    int w, h;
    int dayOne = 6, month = 1, year = 2000, day = 27, selected = 30;
    int thisYear, thisMonth, ethisYear, ethisMonth;
    int emonth, eyear, eday, edayOne, eselected;
    Calendar c;
    int[] lastday = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    ECalendar eCalendar;
    EthiopicCalendar etcal;
    boolean showEt = true, useGeez = false, showMenu = false;
    private int ccol;
    private int crow;
    private int scol;
    private int srow;
    private int escol;
    private int esrow;
    private int eccol;
    private int ecrow;
    private String[][] menu = {{"Goto Today", "Date Converter", "Geez numbers", "አማርኛ", "About"}, {"ወደ ዛሬ ሂድ", "የ ቀን መቀየርያ", "የ ግዕዝ ቁጥሮች", "English", "ስለ"}};
    private String[][] About = {{"", "Ethiopian Calendar", "Version 1.0", "made for Tecno Mobile", ""}, {"", "የ ኢትዮጵያ ቀን መቁመርያ", "ዕትም ፩", "ለ Tecno የ ተሰራ", ""}};
    int menuSelect = 0;
    private boolean showAbout = false;
    public int lang = 0;
    String btn[][] = {{"options", "Toggle", "Exit", "Select", "Back"}, {"መቼት", "ቀይር", "ዉጣ", "ምረጥ", "ተመለስ"}};
    Preferences pref;
    private Font font = Font.getDefaultFont();
    Image orgImg, tmpImg, newImg;
    int width,height;
    //</editor-fold>

    /**
     * constructor
     */
    public Ccanvas(ECalendar EC) {
        try {
            pref = new Preferences("Saver");
        } catch (RecordStoreException ex) {
        }

        try {
            showEt = pref.get("showEt").equals(String.valueOf(true));
            useGeez = pref.get("useGeez").equals(String.valueOf(true));
            lang = Integer.parseInt(pref.get("lang"));
            L.selected = lang;


        } catch (Exception ex) {
        }
        if (useGeez) {
            menu[0][2] = "Arabic numbers";
            menu[1][2] = "የ አረብ ቁጥሮች";
        }
        eCalendar = EC;
        try {
            bar_en = Image.createImage("/bar_en.png");
            bar_et = Image.createImage("/bar_et.png");
            calendar_en = Image.createImage("/calendar_en.png");
            calendar_et = Image.createImage("/calendar_et.png");
            current = Image.createImage("/current.png");
            month_en = Image.createImage("/month_en.png");
            month_et = Image.createImage("/month_et.png");
            selector = Image.createImage("/selector.png");
            title = Image.createImage("/title_en.png");
            year_en = Image.createImage("/year_en.png");
            year_et = Image.createImage("/year_et.png");
            bg = Image.createImage("/bg.png");
        } catch (IOException ex) {
        }
        this.setFullScreenMode(true);
        w = 240;
        h = 320;
        c = Calendar.getInstance();
        reset();
        if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
            lastday[1] = 29;
        }
        width=getWidth();
        height=getHeight();
        orgImg = Image.createImage(240, 320);
        tmpImg = Image.createImage(width, 320);
        newImg = Image.createImage(width,height);
    }

    /**
     * paint
     */
    public void paint(Graphics g) {
        paint2Image(g);
        if (width < 240 ||height < 240) {
            resizeImage();
            g.drawImage(newImg,width / 2,height / 2, Graphics.HCENTER | Graphics.VCENTER);
        }
    }

    /**
     * Called when a key is pressed.
     */
    protected void keyPressed(int keyCode) {
        switch (keyCode) {
            case 0://dummy call from keypressed
                break;
            case -6://left soft key
            case 113:
                if (!showMenu && !showAbout) {
                    showMenu = true;
                }
                break;
            case -7://right soft key
            case 112:
                if (showMenu || showAbout) {
                    showMenu = false;
                    showAbout = false;
                    break;
                }
                pref.put("showEt", String.valueOf(showEt));
                pref.put("useGeez", String.valueOf(useGeez));
                pref.put("lang", String.valueOf(lang));
                try {
                    pref.save();
                } catch (RecordStoreException ex) {
                }
                eCalendar.notifyDestroyed();
                break;
            case -1://up arrow key
            case 50:
            case 1:
                if (showMenu) {
                    menuSelect = Math.max(--menuSelect, 0);
                } else if (showAbout) {
                    return;
                } else {
                    up();
                }
                break;
            case -2://down arrow key
            case 56:
            case 6:
                if (showMenu) {
                    menuSelect = Math.min(++menuSelect, menu[lang].length - 1);
                    break;
                } else if (showAbout) {
                    break;
                }
                down();
                break;
            case -3://left arrow key
            case 52:
            case 2:
                if (showAbout || showMenu) {
                    break;
                }
                left();
                break;
            case -4://right arrow key
            case 54:
            case 5:
                if (showAbout || showMenu) {
                    break;
                }
                right();
                break;
            case -5://fire key
            case 53:
            case -8:
                if (showMenu) {
                    switch (menuSelect) {
                        case 0:
                            reset();
                            break;
                        case 1:
                            eCalendar.show(new DateConverter(eCalendar));
                            break;
                        case 2:
                            useGeez = !useGeez;
                            if (useGeez) {
                                menu[0][menuSelect] = "Arabic numbers";
                                menu[1][menuSelect] = "የ አረብ ቁጥሮች";
                            } else {
                                menu[0][menuSelect] = "Geez numbers";
                                menu[1][menuSelect] = "የ ግዕዝ ቁጥሮች";
                            }
                            break;
                        case 3:
                            lang = (++lang) % 2;
                            L.selected = lang;
                            break;
                        case 4:
                            showAbout = true;
                            break;
                        default:
                            break;
                    }
                    showMenu = false;
                    break;
                } else if (showAbout) {
                    return;
                }
//                System.out.println(year * 10000 + month * 100 + selected);
                if (year * 10000 + month * 100 + selected >= 20070812) {
                    showEt = !showEt;
                }
                break;
            default:
                return;
        }
        updateEt();
        repaint();
    }

    protected void pointerReleased(int x, int y) {
        if (y > (h * 92) / 100 && x < 2 * w / 3) {
            keyReleased(-5);
        }
    }

    protected void pointerPressed(int x, int y) {
//        System.out.println("show menu:" + showMenu);
        if (showMenu) {
            int fh = font.getHeight();
            int height = menu[lang].length * fh;
            int fw = font.stringWidth(menu[lang][0]) + 12;
            int width = Math.max(fw, (3 * w) / 4);
//            System.out.println("x:" + x);
//            System.out.println("y:" + y);
//            System.out.println("x Range:" + ((w - width) / 2 + 3) + "-" + ((w + width) / 2 - 3));
//            System.out.println("y Start:" + ((h - height) / 2 + 0 * fh));
            if (x > (w - width) / 2 + 3 && x < (w + width) / 2 - 3) {
                for (int i = 0; i < menu[lang].length; i++) {
                    if (y > (h - height) / 2 + i * fh && y < (h - height) / 2 + (i + 1) * fh) {
//                        System.out.println("selected " + i);
                        menuSelect = i;
                        keyPressed(-5);     //simulate fire key
                        return;
                    }

                }
            }
            return;
        }
        if (y > 20 && y < 40) {
            if (x > w / 2 - 85 && x < w / 2 - 65) {
                selected = 1;
                keyPressed(-3);      //simulate left key pressed
            } else if (x > w / 2 + 65 && x < w / 2 + 85) {
                selected = lastday[month];
                keyPressed(-4);      //simulated right key pressed
            }
            return;
        }
        if (y > (h * 92) / 100) {
            if (x < w / 3) {
                keyPressed(-6);     //simulated left soft key
            } else if (x < 2 * w / 3) {
                keyPressed(-5);     //simulated fire key
            } else {
                keyPressed(-7);       //simulated right soft key
            }

        }
        if (y > 75) {
            if (showEt) {
                return;
            }
            if (y < 95) {

                checkX(x, 0);
            } else if (y < 115) {
                checkX(x, 1);
            } else if (y < 135) {
                checkX(x, 2);
            } else if (y < 155) {
                checkX(x, 3);
            } else if (y < 175) {
                checkX(x, 4);
            } else if (y < 195) {
                checkX(x, 5);
            }
        }
    }

    private void callCell(int row, int col) {
        if (showAbout || showMenu) {
            return;
        }
        if (row * 7 + col < 6 - dayOne || row * 7 + col + dayOne - 5 > lastday[month]) {
            return;
        }
        selected = row * 7 + col + dayOne - 5;
        keyPressed(0);
    }

    public void buttons(Graphics g, String left, String center, String right, int w, int h) {
        g.setColor(0x5AE7EF);
        g.fillRect(0, (h * 92) / 100, w, (8 * h) / 100 + 1);
        g.setColor(0x000000);
        g.drawString(left, 8, (98 * h) / 100, Graphics.BASELINE | Graphics.LEFT);
        g.drawString(center, w / 2, (98 * h) / 100, Graphics.BASELINE | Graphics.HCENTER);
        g.drawString(right, w - 8, (98 * h) / 100, Graphics.BASELINE | Graphics.RIGHT);
    }

    private void clear(Graphics g, int day, int dayOne) {
        int tdy = 6 - dayOne + day - 1;
        int scol = (tdy) % 7;
        int srow = (tdy - scol) / 7;
        g.setClip((w - 210) / 2 + scol * 30, 2 + 75 + srow * 20, 30, 20);
        g.drawImage(bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
        g.setClip(0, 0, w, h);
    }

    protected void sizeChanged(int w, int h) {
        this.w = w;
        this.h = h;
    }

    private void row_col() {
        int tdy = 6 - dayOne + selected;
        scol = (tdy) % 7;
        srow = (tdy - scol) / 7;
        tdy = 6 - dayOne + day;
        ccol = tdy % 7;
        crow = (tdy - ccol) / 7;
        if (ccol == 0) {
            ccol = 7;
            crow--;
        }
        if (scol == 0) {
            scol = 7;
            srow--;
        }

    }

    private void erow_col() {
        int tdy = 6 - edayOne + eselected;
        escol = (tdy) % 7;
        esrow = (tdy - escol) / 7;
        tdy = 6 - edayOne + eday;
        eccol = tdy % 7;
        ecrow = (tdy - eccol) / 7;
        if (escol == 0) {
            escol = 7;
            esrow--;
        }
        if (eccol == 0) {
            eccol = 7;
            ecrow--;
        }
    }

    public static void popup(Graphics g, String[] items, int selected, int w, int h) {
        Font f = g.getFont();
        int fh = f.getHeight();
        int height = items.length * fh;
        int fw = f.stringWidth(items[1]) + 12;
        int width = Math.max(fw, (3 * w) / 4);
        image(g, 0xD0B0B0B0, w, h, width, height);
        g.setColor(0xF89210);
        if (selected != -1) {
            g.fillRoundRect((w - width) / 2 + 3, (h - height) / 2 + selected * fh, width - 6, fh, 3, 3);
            g.setColor(0x000000);
        }
        g.setColor(0x000000);
        for (int i = 0; i < items.length; i++) {
            g.drawString(items[i], w / 2, (h - height) / 2 + i * fh + (3 * fh) / 4, Graphics.HCENTER | Graphics.BASELINE);
        }

    }

    public static void image(Graphics g, int color, int w, int h, int width, int height) {
        int rgb[] = new int[width * (height + 20)];
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = color;
        }
        Image img = Image.createRGBImage(rgb, width, height + 20, true);
        g.drawImage(img, (w - width) / 2, (h - height) / 2 - 10, Graphics.LEFT | Graphics.TOP);
    }

    private void checkX(int x, int row) {
        if (x > (w - 210) / 2) {
            if (x < (w - 210) / 2 + 30) {
                callCell(row, 0);
            } else if (x < (w - 210) / 2 + 60) {
                callCell(row, 1);
            } else if (x < (w - 210) / 2 + 90) {
                callCell(row, 2);
            } else if (x < (w - 210) / 2 + 120) {
                callCell(row, 3);
            } else if (x < (w - 210) / 2 + 150) {
                callCell(row, 4);
            } else if (x < (w - 210) / 2 + 180) {
                callCell(row, 5);
            } else if (x < (w - 210) / 2 + 210) {
                callCell(row, 6);
            }
        }
    }

    private void updateEt() {
        int[] dates = etcal.gregorianToEthiopic(year, month + 1, selected);
        eyear = dates[0];
        emonth = dates[1];

        dates = etcal.ethiopicToGregorian(eyear, emonth, 1);
        edayOne = EthiopicCalendar.DayOfWeek(dates[0], dates[1], dates[2]);
        if (edayOne != 0) {
            edayOne = 7 - edayOne;
        }
        eselected = etcal.gregorianToEthiopic(year, month + 1, selected)[2];
    }

    private boolean up() {
        if (selected < 8) {
            if (month > 0) {
                month--;
            } else {
                if (year == 2000) {
                    return true;
                }
                month = 11;
                year--;
                if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
                    lastday[1] = 29;
                } else {
                    lastday[1] = 28;
                }
            }
            dayOne = EthiopicCalendar.DayOfWeek(year, month + 1, 1);
            if (dayOne != 0) {
                dayOne = 7 - dayOne;
            }
            selected = selected - 7 + lastday[month];
        } else {
            selected -= 7;
        }
        return false;
    }

    private boolean down() {
        if (lastday[month] - 6 <= selected) {
            selected = selected + 7 - lastday[month];
            if (month == 11) {
                if (year == 2020) {
                    return true;
                }
                year++;
                if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
                    lastday[1] = 29;
                } else {
                    lastday[1] = 28;
                }
            }
            month = (++month) % 12;
            selected = Math.min(selected, lastday[month]);
            dayOne = EthiopicCalendar.DayOfWeek(year, month + 1, 1);
            if (dayOne != 0) {
                dayOne = 7 - dayOne;
            }

        } else {
            selected += 7;
        }
        return false;
    }

    private boolean left() {
        if (selected == 1) {
            if (month == 0) {
                if (year == 2000) {
                    return true;
                }
                month = 11;
                --year;
                if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
                    lastday[1] = 29;
                } else {
                    lastday[1] = 28;
                }
            } else {
                --month;
            }
            selected = lastday[month];
            dayOne = EthiopicCalendar.DayOfWeek(year, month + 1, 1);
            if (dayOne != 0) {
                dayOne = 7 - dayOne;
            }
        } else {
            --selected;
        }
        return false;
    }

    private boolean right() {
        if (selected == lastday[month]) {
            if (month == 11) {
                if (year == 2020) {
                    return true;
                }
                month = 0;
                year++;
                if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
                    lastday[1] = 29;
                } else {
                    lastday[1] = 28;
                }
                selected = 1;
            } else {
                month++;
                selected = 1;
            }
            dayOne = EthiopicCalendar.DayOfWeek(year, month + 1, 1);
            if (dayOne != 0) {
                dayOne = 7 - dayOne;
            }
        } else {
            ++selected;
        }
        return false;
    }

    private String holiday(int month, int day) {
        String temp[] = null;
        int dt = month * 100 + day;
        switch (dt) {
            case 101:   //new Year
                temp = new String[]{"Ethiopian New Year", "እንቁጣጣሽ"};
                break;
            case 117:   //christmas
                temp = new String[]{"Finding of the true Cross", "የ መስቀል በዓል"};
                break;
            case 429:
                temp = new String[]{"Ethiopian X-mass", "የ ገና በዓል"};
                break;
            case 511:
                temp = new String[]{"Epiphany", "የ ጥምቀት በዓል"};
                break;
            case 623:
                temp = new String[]{"Victory at Adwa Day", "የ ዓድዋ ድል በዓል"};
                break;
            case 823:
                temp = new String[]{"Labour Day", "የ ላብኣደሮች ቀን"};
                break;
            case 827:
                temp = new String[]{"Patriots' Day", "የ አርበኞች ቀን"};
                break;
            case 920:
                temp = new String[]{"Derg Downfall Day", "ደርግ የወደቀበት ቀን"};
                break;
            default:
                temp = new String[]{"", ""};
        }
        return temp[lang];
    }

    private void reset() {
        selected = c.get(Calendar.DAY_OF_MONTH);
        day = selected;
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        thisMonth = month;
        thisYear = year;
        dayOne = EthiopicCalendar.DayOfWeek(year, month + 1, 1);
        if (dayOne != 0) {
            dayOne = 7 - dayOne;
        }
        etcal = new EthiopicCalendar();
        int[] dates = etcal.gregorianToEthiopic(year, month + 1, day);
        eyear = dates[0];
        emonth = dates[1];
        eday = dates[2];
        ethisYear = eyear;
        ethisMonth = emonth;
        eselected = eday;
        edayOne = 2;
        dates = etcal.ethiopicToGregorian(eyear, emonth, 1);
        edayOne = EthiopicCalendar.DayOfWeek(dates[0], dates[1], dates[2]);
        if (edayOne != 0) {
            edayOne = 7 - edayOne;
        }
    }

    private void paint2Image(Graphics g) {
        if (width < 240 ||height < 240) {
            g = orgImg.getGraphics();
        } else {
            w =width;
            h =height;
        }
        row_col();
        erow_col();

        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, w, h);
        g.drawImage(bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
        g.drawImage(title, w / 2, 10, Graphics.HCENTER | Graphics.TOP);
        if (showEt) {
            g.drawRegion(month_et, 0, (emonth - 1) * 20, 60, 20, 0, w / 2 + 5, 28, Graphics.VCENTER | Graphics.RIGHT);

            if (useGeez) {
                g.drawRegion(year_et, 0, (eyear - 2000) * 20, 60, 20, 0, w / 2 - 5, 28, Graphics.VCENTER | Graphics.LEFT);
                g.drawImage(bar_et, w / 2, 50, Graphics.HCENTER | Graphics.TOP);
                g.drawRegion(calendar_et, edayOne * 30, 0, 210, 120, 0, w / 2, 75, Graphics.HCENTER | Graphics.TOP);
            } else {
                g.drawRegion(year_en, 0, (eyear - 2000) * 20, 60, 20, 0, w / 2 - 5, 28, Graphics.VCENTER | Graphics.LEFT);
                g.drawImage(bar_et, w / 2, 50, Graphics.HCENTER | Graphics.TOP);
                g.drawRegion(calendar_en, edayOne * 30, 0, 210, 120, 0, w / 2, 75, Graphics.HCENTER | Graphics.TOP);
            }
            g.drawImage(current, 2 + (w - 210) / 2 + escol * 30, 75 + esrow * 20, Graphics.RIGHT | Graphics.TOP);
            if (ethisYear == eyear && ethisMonth == emonth) {

                g.drawImage(selector, (w - 210) / 2 + eccol * 30, 2 + 75 + ecrow * 20, Graphics.RIGHT | Graphics.TOP);
            }
            if (emonth == 13) {
                for (int i = 7; i < 31; i++) {
                    clear(g, i, edayOne);
                }
                if ((eyear + 1) % 4 != 0) {
                    clear(g, 6, edayOne);
                }
            }
            clear(g, 31, edayOne);
        } else {

            g.drawRegion(month_en, 0, month * 20, 60, 20, 0, w / 2 + 5, 28, Graphics.VCENTER | Graphics.RIGHT);
            g.drawRegion(year_en, 0, (year - 2000) * 20, 60, 20, 0, w / 2 - 5, 28, Graphics.VCENTER | Graphics.LEFT);
            g.drawImage(bar_en, w / 2, 50, Graphics.HCENTER | Graphics.TOP);
            g.drawRegion(calendar_en, dayOne * 30, 0, 210, 120, 0, w / 2, 75, Graphics.HCENTER | Graphics.TOP);
            g.drawImage(current, 2 + (w - 210) / 2 + scol * 30, 75 + srow * 20, Graphics.RIGHT | Graphics.TOP);
            if (thisYear == year && thisMonth == month) {
                g.drawImage(selector, (w - 210) / 2 + ccol * 30, 2 + 75 + crow * 20, Graphics.RIGHT | Graphics.TOP);
            }
            for (int k = 31; k > lastday[month]; k--) {
                clear(g, k, dayOne);
            }
        }
        g.setColor(0x000000);
        g.drawLine((w - 240) / 2 + 10, 200, (w + 240) / 2 - 20, 200);
//        System.out.println(emonth * 100 + eselected);
        g.drawString(holiday(emonth, eselected), (w - 240) / 2 + 10, 208, Graphics.TOP | Graphics.LEFT);
//        if(hDay() !=null)
//            g.drawString("[] "+hDay(), (w - 240) / 2 + 10, 28, Graphics.TOP|Graphics.RIGHT);
        int fh = g.getFont().getHeight();

        buttons(g, btn[lang][0], btn[lang][1], btn[lang][2], w, h);
        if (showMenu) {
            popup(g, menu[lang], menuSelect, w, h);
            buttons(g, "", btn[lang][3], btn[lang][4], w, h);
        } else if (showAbout) {
            popup(g, About[lang], -1, w, h);
            buttons(g, "", "", btn[lang][4], w, h);
        }
    }

    private void resizeImage() {
        int hRatio = (1000 * 320) /height;
        int wRatio = (1000 * 240) /width;
        Graphics tmpG = tmpImg.getGraphics();
        Graphics newG = newImg.getGraphics();

        for (int i = 0; i <width; i++) {
            tmpG.setClip(i, 0, 1, 320);
            tmpG.drawImage(orgImg, i - (wRatio * i) / 1000, 0, Graphics.LEFT | Graphics.TOP);

        }
        for (int i = 0; i <height; i++) {
            newG.setClip(0, i,width, 1);
            newG.drawImage(tmpImg, 0, i - (hRatio * i) / 1000, Graphics.LEFT | Graphics.TOP);

        }
    }
}