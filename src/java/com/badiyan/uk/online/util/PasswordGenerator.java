package com.badiyan.uk.online.util;

import com.badiyan.torque.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


public class
PasswordGenerator
{
	// CLASS VARIABLES

	private static Random randy = new Random();
	private static String password_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String[] bad_words = {"SHIT", "FUCK", "DAMN", "ASS", "TARD", "HELL", "SLUT", "CUNT", "COCK", "DICK", "GOD", "FUK", "FUC"};

	// CLASS METHODS

	public static String
	getPassword(int _length)
	{
		StringBuffer password = null;
		boolean contains_bad_word = true;
		while (contains_bad_word)
		{
			contains_bad_word = false;
			password = new StringBuffer();
			for (int i = 0; i < _length; i++)
			{
				int char_index = Math.abs(randy.nextInt() % 26);
				password.append(password_chars.charAt(char_index));
			}

			for (int i = 0; i < bad_words.length; i++)
			{
				String bad_word = bad_words[i];
				if (password.indexOf(bad_word) != -1)
					contains_bad_word = true;
			}
		}
		return password.toString();
	}

	public static boolean
	isUnique(String _password)
		throws TorqueException
	{
		// ensure that this is a unique password

		Criteria crit = new Criteria();
		crit.add(PersonPeer.PASSWORD, _password);
		List obj_list = PersonPeer.doSelect(crit);
		return (obj_list.size() == 0);
	}
	
	/*
	 *  try {

            // collect correct inputs or DIE.
            String email = args[0];
            Color fg = new Color(Integer.parseInt(args[1], 16));
            Color bg = new Color(Integer.parseInt(args[2], 16));
            String filename = args[3];

            // call render image method.
            RenderedImage rendImage = writeImage(email, fg, bg);

            File file = new File(filename);

            ImageIO.write(rendImage, "png", file);

        } catch (Exception e) {
            // Sloppy Error handling below
            System.out.println("Usage: textToImage.jar email fg-colour-hex bg-colour-hex filename");
            System.out.println("Example: textToImage.jar eg@eg.com FFFFFF 0000FF C:\\dir\\image.png");
            System.out.print(e.getMessage());
        }
	 */

    private static RenderedImage writeImage(String text, Color fgc, Color bgc) {

        // calculate image size requirements.
        int width = (text.length() * 7) + 5;

        // standard height requirement of 16 px.
        int height = 16;
        BufferedImage buffRenderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D flatGraphic = buffRenderImage.createGraphics();


        // Draw background
        flatGraphic.setColor(bgc);
        flatGraphic.fillRect(0, 0, width, height);

        //Draw text
        flatGraphic.setColor(fgc);
        Font font = new Font("Courier", Font.BOLD, 12);
        flatGraphic.setFont(font);
        flatGraphic.drawString(text, 1, 10);

        // don't use drawn graphic anymore.
        flatGraphic.dispose();

        return buffRenderImage;
    }
}


