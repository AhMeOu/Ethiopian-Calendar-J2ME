import javax.microedition.lcdui.*;
/**
 * @author Ahmed
 */
public class DateConverter extends Canvas implements Runnable {
    private static final int WHITE = 0xFFFFFF;
    private static final int BLUE = 0x6666E9;
    private static final int GRAY = 0x606060;
    private static final int BACKGROUND = 0xEEEEEE;
    private static String input1 = "", input2 = "";
    private ECalendar midlet;
    private int width, height, selected = 0,
            popupSelected = 0, popupCurrent = 0;
    private EthiopicCalendar calendar = new EthiopicCalendar();
    private String from = L.Gregorian[L.selected], to = L.Ethiopic[L.selected];
    private static String[] Conversions;
    private String[] Days = {L.Sun[L.selected], L.Mon[L.selected], L.Tue[L.selected], L.Wed[L.selected], L.Thr[L.selected], L.Fri[L.selected], L.Sat[L.selected]};
    private int result[] = {0, 0, 0}, dayOfWeak = 0;
    private boolean popup = false;
    private String formatedResult = "";
    private int padding = 0;
    private Thread t = new Thread(this);
    private boolean run = false, cur = false;
    public DateConverter(ECalendar midlet) {
        this.midlet = midlet;
        setFullScreenMode(true);
        height = getHeight();
        width = getWidth();
        Conversions = new String[]{L.Gregorian[L.selected] + " - " + L.Ethiopic[L.selected], L.Ethiopic[L.selected] + " - " + L.Gregorian[L.selected]};
       }

    /**
     * paint
     */
    public void paint(Graphics g) {
        Font f = g.getFont();
        int bl = f.getBaselinePosition();
        int fh = f.getHeight();
        int tw = f.stringWidth(Conversions[0]);

        if (height < (4200 + 800 * fh) / 92) {// compress content for smaller screens
            padding = 5 + fh;
        }
        bGround(g, L.Date_Converter[L.selected], width, height);

        //Conversion selector box
        if (selected == 0) {
            g.setColor(0xB0C5E3);
        } else {
            g.setColor(WHITE);
        }
        g.fillRoundRect((width - tw) / 2 - 8, height / 10 + 5, tw + 16, fh + 6, 5, 5);
        if (selected == 0) {
            g.setColor(BLUE);
        } else {
            g.setColor(GRAY);
        }
        g.drawRoundRect((width - tw) / 2 - 8, height / 10 + 5, tw + 16, fh + 6, 5, 5);
        g.setColor(GRAY);
        g.drawString(Conversions[popupCurrent], width / 2, height / 10 + 8 + bl, Graphics.BASELINE | Graphics.HCENTER);

        // Result box
        g.setColor(GRAY);
        g.drawRoundRect(10, height / 10 + 39 + 4 * fh - 2 * padding, width - 20, 4 * fh - padding, 5, 5);
        g.setColor(BACKGROUND);
        int rh = f.stringWidth(to);
        //g.fillRect(20, height / 10 + 39 + 4 * fh - bl - 2 * padding, rh + 10, fh);
        g.drawLine(20, height / 10 + 39 + 4 * fh - 2 * padding, rh + 30, height / 10 + 39 + 4 * fh - 2 * padding);
        g.setColor(GRAY);
        g.drawString(to, 25, height / 10 + 39 + 4 * fh + 5 - 2 * padding, Graphics.BASELINE | Graphics.LEFT);
        g.drawString(formatedResult, width / 2, height / 10 + 39 + 6 * fh - 2 * padding, Graphics.BASELINE | Graphics.HCENTER);

        //Date input box
        tw = f.stringWidth("Gregorian");
        g.drawString(from, 5 + tw, height / 10 + 22 + 2 * fh + bl - padding, Graphics.BASELINE | Graphics.RIGHT);
        if (selected == 1) {
            g.setColor(0xB0C5E3);
        } else {
            g.setColor(WHITE);
        }
        g.fillRoundRect(8 + tw, height / 10 + 20 + 2 * fh - padding, width - tw - 16, fh + 6, 5, 5);
        if (selected == 1) {
            g.setColor(BLUE);
        } else {
            g.setColor(GRAY);
        }
        g.drawRoundRect(8 + tw, height / 10 + 20 + 2 * fh - padding, width - tw - 16, fh + 6, 5, 5);
        //user input text
        if (!run) {
            g.drawString("DD-MM-YYYY", 11 + tw, height / 10 + 23 + 2 * fh + bl - padding, Graphics.BASELINE | Graphics.LEFT);
        } else {
            g.setColor(0);
            g.drawString(input1 + input2, 11 + tw, height / 10 + 23 + 2 * fh + bl - padding, Graphics.BASELINE | Graphics.LEFT);
            if (cur) {
                g.drawLine(11 + tw + f.stringWidth(input1), height / 10 + 23 + 2 * fh - padding, 11 + tw + f.stringWidth(input1), height / 10 + 23 + 3 * fh - padding);
            }
        }
        if (popup) {
            buttons(g, L.Change[L.selected], L.Cancel[L.selected], width, height);
        } else if (selected == 0) {
            buttons(g, L.Change[L.selected], L.Back[L.selected], width, height);
        } else if (input1.length() > 0) {
            buttons(g, L.Ok[L.selected], L.Clear[L.selected], width, height);
        } else {
            buttons(g, L.Ok[L.selected], L.Back[L.selected], width, height);
        }

        if (popup) {
            popup(g, Conversions, popupSelected, width, height);

        }
    }

    /**
     * Called when a key is pressed.
     */
    protected void keyPressed(int keyCode) {
        switch (keyCode) {
            case -6://left soft key
            case -5://fire key

                if (popup) {
                    popupCurrent = popupSelected;
                    int delim = Conversions[popupCurrent].indexOf(' ');
                    from = Conversions[popupCurrent].substring(0, delim);
                    to = Conversions[popupCurrent].substring(delim + 3);
                    popup = false;
                } else if (selected == 0) {
                    popup = true;
                } else {//Calculate Date
                    if (Evaluator.verifyDate(input1 + input2, popupCurrent)) {
                        result = Evaluator.parseDate(input1 + input2);
                        result = evaluate(result);

                        formatedResult = Days[dayOfWeak] + ". " + result[2] + '-' + result[1] + '-' + result[0];
                    } else {
                        formatedResult = Evaluator.errMsg;
                    }
                }
                break;
            case -1://up arrow key
                if (popup) {
                    popupSelected = (popupSelected == 0) ? Conversions.length - 1 : popupSelected - 1;
                } else {
                    if (selected == 0) {
                        selected = 1;
                        run = true;
                        new Thread(this).start();
                        break;
                    } else {
                        selected = 0;
                        run = false;
                        cur = false;
                    }

                }
                break;
            case -2://down arrow key
                if (popup) {
                    popupSelected = (popupSelected == Conversions.length - 1) ? 0 : popupSelected + 1;
                } else {
                    if (selected == 0) {
                        selected = 1;

                        run = true;
                        new Thread(this).start();
                        break;

                    } else {
                        selected = 0;
                        run = false;
                        cur = false;
                    }
                }
                break;
            case -3://left arrow key
                if (selected == 1 && input1.length() > 0) {
                    input2 = input1.charAt(input1.length() - 1) + input2;
                    input1 = input1.substring(0, input1.length() - 1);
                }
                break;
            case -4://right arrow key
                if (selected == 1 && input2.length() > 0) {
                    input1 += input2.charAt(0);
                    input2 = input2.substring(1);
                }
                break;
            case 48://num 0
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '0';
                }
                break;
            case 49://num 1
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '1';
                }
                break;
            case 50://num 2
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '2';
                }
                break;
            case 51://num 3
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '3';
                }
                break;
            case 52://num 4
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '4';
                }
                break;
            case 53://num 5
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '5';
                }
                break;
            case 54://num 6
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '6';
                }
                break;
            case 55://num 7
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '7';
                }
                break;
            case 56://num 8
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '8';
                }
                break;
            case 57://num 9
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '9';
                }
                break;
            case 42://key star used as minus sign
                if (selected == 1 && (input1 + input2).length() < 10) {
                    input1 += '-';
                }
                break;
            case -7://right soft key
            case -8://hardware cancel key

                if (selected == 1 && input1.length() > 0) {
                    input1 = input1.substring(0, input1.length() - 1);
                } else if (popup) {
                    popup = false;
                } else {
                    run = false;
                    selected = 0;
                    midlet.show(midlet.cal);
                }
                break;

            default:
                break;
        }
        repaint();

    }

    /**
     * a Thread to control the blinking cursor
     */
    public void run() {
        while (run) {
            if (selected == 1) {
                cur = !cur;
                repaint();
                try {
                    t.sleep(500);
                } catch (InterruptedException ex) {
                }
            }

        }

    }

    private int[] evaluate(int[] result) {//format DD MM YYYY
        calendar.set(result[2], result[1], result[0]);
        int[] converted = result;

        if (to.charAt(0) == 'E' || to.charAt(0) == '\u12A2')//Gregorian to Ethiopic
        {
            dayOfWeak = EthiopicCalendar.DayOfWeek(result[2], result[1], result[0]);
            converted = calendar.gregorianToEthiopic();
        }

        if (to.charAt(0) == 'G' || to.charAt(0) == '\u1348')//Ethiopic to Gregorian
        {
            converted = calendar.ethiopicToGregorian();
            dayOfWeak = EthiopicCalendar.DayOfWeek(converted[0], converted[1], converted[2]);
        }

        return converted;
    }

    private void buttons(Graphics g, String left, String right, int w, int h) {
        g.setColor(0x5AE7EF);
        g.fillRect(0, (h * 92) / 100, w, (8 * h) / 100 + 1);
        g.setColor(0x000000);
        g.drawString(left, 8, (98 * h) / 100, Graphics.BASELINE | Graphics.LEFT);
        g.drawString(right, w - 8, (98 * h) / 100, Graphics.BASELINE | Graphics.RIGHT);
    }

    private void popup(Graphics g, String[] items, int popupSelected, int w, int h) {
        Font f = g.getFont();
        int fh = f.getHeight();
        int height = items.length * fh;
        int fw = f.stringWidth(items[1]) + 12;
        int width = Math.max(fw, (3 * w) / 4);
        Ccanvas.image(g, 0xD0B0B0B0, w, h, width, height);
        g.setColor(0xF89210);
        if (selected != -1) {
            g.fillRoundRect((w - width) / 2 + 3, (h - height) / 2 + popupSelected * fh, width - 6, fh, 3, 3);
            g.setColor(0x000000);
        }
        g.setColor(0x000000);
        for (int i = 0; i < items.length; i++) {
            g.drawString(items[i], w / 2, (h - height) / 2 + i * fh + (3 * fh) / 4, Graphics.HCENTER | Graphics.BASELINE);
        }
    }

    private void bGround(Graphics g, String title, int w, int h) {
        g.drawImage(midlet.cal.bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
        g.setColor(0x5AE7EF);
        g.fillRect(0, 0, w, h / 10);
        g.setColor(0xFFFFFF);
        g.drawString(title, w / 2, h / 15, Graphics.BASELINE | Graphics.HCENTER);
    }
}
