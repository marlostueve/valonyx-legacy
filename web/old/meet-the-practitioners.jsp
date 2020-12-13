<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
	<link rel="stylesheet" type="text/css" href="css/mystyle2.css" />
	<title>Welcome to Valeo</title>
    </head>
    <body>

	<div id="main">
	    
<%@ include file="channels\channel-header.jsp" %>
	    <div id="bodyline">
		
		<div id="todaywrapper">

		    <div id="today">

			<div id="serviceswhere">
			    
			    <div id="services-wide">
				<h2><strong>Dr. Craig Hartman</strong></h2>
				<p><img src="images/crag hartman.jpg" style="float: left;" alt="">I have been interested in nutrition for many years but really got to experience the power of good nutrition several years ago.  At that time, my tongue started to tingle.  At first it happened only when I was stressed or fatigued but eventually, it became a daily occurrence.  I saw many doctors without getting any answers.  Finally, I just happened to go to a nutrition seminar where a doctor was teaching a form of nutritional analysis.  He did an analysis on me and discovered a mercury toxicity. After two weeks of taking the nutritional products he recommended, I started to notice improvement.  Since that time, not only has my tongue improved but I have less fatigue, am less cold-sensitive and have fewer body aches including the chronic aching feet I have had since childhood.</p>
				    <p>I have been a chiropractor since 1990 and had my own practice until 2008 when I joined Valeo Wellness Center.  My career in chiropractic and nutritional health care got started because of my interest in attaining and maintaining health using natural health care methods.  During my years of practice, I have continually worked to improve the care I deliver by taking many chiropractic and nutrition seminars, becoming a Certified Chiropractic Sports Physician and a Certified Addictionologist and, in the spring of 2009, I will complete a Masters Degree program in Human Nutrition.</p>
				    <p>I am married and have two wonderful daughters.  My interest in nutrition is partly selfish in that I want to be able to provide the best opportunity for my family to lead a healthy, active life but I also want to provide that same opportunity for my patients.  Nutrition affects every function of the body and without good nutrition, it is impossible for your body to reach its full health potential.   </p>
				    <p>I am very excited about the opportunity to help people with nutritional care and look forward to working with each you!</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Robin Kragness</strong></h2>
				<p>My wellness journey began in Santa Monica, California after graduating from UCLA.  I studied English Literature to receive my bachelor's degree, but my true abiding passion has always been physical fitness and God's ultimate design for our health.  So after college I began to educate myself on how to achieve my own sense of core balance and through years in the field of education, developed the ability to teach and encourage others to go after the quality of life and health we were meant to enjoy.</p>
				    <p>Before coming to join the Valeo team, I was working as a Wellness Technician and Health Coach at a local clinic.  I received a tremendous education on how technology, various modalities and testing can help people progress on their path to wellness.  I specifically worked in the areas of Cranial and Breast Thermography, Meridian Stress Assessment, Lymphatic Drainage Therapy, Bone Density Scanning, Cardio Pulsewave Screening, and Chiropractic Care.  With an emphasis on Weight Management, Exercise and Nutritional Planning, I enjoy working one-on-one with individuals to help them reach their health goals.</p>
				    <p>I live in Eden Prairie with my husband of 18 years, Scott, and our two spirited redheads, Hailey (14) and Hudson (12).  We've been in Minnesota for 5 years after a 12 year adventure living on the island of Maui.  I picked up a magazine article about a year ago which featured the Valeo Health & Wellness Center.  After meeting the staff and feeling a genuine sense of community, compassion and care for individual patients, I knew this was the place to "pursue the things over which Christ presides" (Colossians 3:1, The Message).</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Laura Cady</strong></h2>
				<p>I graduated with honors and 1200 hours of education from the Utah College of Massage Therapy, an ACCET and COMTA accredited school in 2000. The massage modalities I use are relaxation, therapeutic, trigger point, neuromuscular, reflexology, craniosacral, and myofascial and I continue my advance training through continuing education seminars.  I became a massage therapist because I always wanted to take care of people and make them feel better. I love what I do. When I am not working I love to work out. Eating right and working out are very important in my life.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Erica Dolan, L.Ac.</strong></h2>
				<p><img src="images/erica dolan.jpg" style="float: left;" alt="">Erica Dolan is a Licensed Acupuncturist (L.Ac.) by the Minnesota Board of Medical Practice and is board certified by the National Certification Commission for Acupuncture and Oriental Medicine (NCCAOM).  She graduated Cum Laude from the Minnesota College of Acupuncture and Oriental Medicine (MCAOM) at Northwestern Health Sciences University in Bloomington, MN where she earned her Master of Acupuncture degree.   Before graduating, Erica treated many patients at internships sites that included the Edith Davis Teaching Clinic and the Courage Center where she treated patients with a variety of health concerns.</p>
				    <p>Erica has always had a passion for helping people with their health.  It wasn't until 2004 that she discovered acupuncture after an auto accident had left her with back pain.  She tried everything to get relief and continued to have pain until someone recommended acupuncture.  She was amazed at the results and the immediate relief from the pain.  This was the spark that lead her to learning more about acupuncture and deciding she wanted to help people and their health thru acupuncture.</p>
				    <p>In her first year of treating patients, she has been honored to make a difference in people's health, to educate people about Traditional Chinese Medicine (TCM) and to witness the positive results acupuncture can have in someone's life.</p>
				    <p>Erica's goal is to listen to each patient and their health concerns and treat naturally, through acupuncture, to help improve each person's quality of health.  She is passionate about what she does and is excited to help you in your healing journey!</p>
				    <p>Erica grew up in Lakeville, MN and graduated from LHS.  Prior to her education at NWHSU, she studied Kinesiology at the University of Minnesota and enjoyed a career in Personal Training.  She currently resides in Savage with her husband, Brian.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Darren Ellingson, CMT, CEAS</strong></h2>
				<p><img src="images/Darren1.jpg" style="float: right;" alt="">My education began when I studied pre-med/pre-chiropractic at Bemidji State University in the early 90's.  Soon thereafter I enrolled in the Minneapolis School of Massage & Bodywork where I obtained my degree in 1996.</p>
				    <p>My first real introduction to "alternative" healthcare began at the age of nine when I saw my mother being treated by a chiropractor for her injuries following a life-changing auto accident.</p>
				    <p>The relief she felt, compared to other traditional treatments she was offered, was indisputable.  This experience, plus spending the day with our local chiropractor were the deciding factors in my brother's decision to devote his life to chiropractic and natural healing.  His enthusiasm only solidified my decision to enter the natural healthcare field.</p>
				    <p>After 11 years in the massage business, my enthusiasm has never waned.  Having worked with professional athletes (Minnesota Timberwolves and others) as well as people of all ages and body types, my enthusiasm continues to grow when I see the potential of natural healing the way I believe God meant it.</p>
				    <p>The modalities I use currently are:  relaxation & deep tissue massage, sports massage, myofascial release, reflexology, and Electro-Acuscope therapy.</p>
				    <p>My specialties include:  prevention and treatment of acute and chronic injuries such as carpal tunnel syndrome, tendonitis, muscle strains & sprains, scar tissue reduction, and a host of other issues.</p>
				    <p>My continued goal is to help you achieve a greater sense of well-being in a holistic manner.  I look forward to serving you.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Dr. Rob Lindsey, Doctor of Chiropractic</strong></h2>
				<p><img src="images/06Familypict2_mod.JPG" style="float: left;" alt="">Dr. Rob Lindsey has been involved with health care for over 15 years.  He graduated from Northwestern College of Chiropractic in 2002.  During his first 10 years he was an Athletic Trainer who utilized physiotherapy, exercise, and rehabilitation to help people recover from injuries.</p>
				    <p>In 1998 he learned something that changed his life.  He learned that in order to reach your God-given potential for health, you must have a nerve system free from any interference.  This lesson caused him to research the history and philosophy of chiropractic and to completely change his approach to health, both philosophically and practically.  He quit his job, went to chiropractic school, and started his first chiropractic practice in January of 2003.</p>
				    <p>Dr. Rob is very passionate and enthusiastic about his life as a chiropractor.  No longer competing with the body to remove aches and pains, he has a much deeper purpose; to focus on the location and correction of vertebral subluxations to reduce nerve interference so that the God-given intelligence of the body can be better expressed.  His mission is to bring glory to God by serving His people through the vehicle of chiropractic care.</p>
				    <p>Dr. Rob lives in Savage with his bride, Denise and their two sons, Elijah (3) and Noah (1 ½). He is active in his church and enjoys traveling, downhill skiing, softball, and camping.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Dr. Aaron Morland, Doctor of Chiropractic</strong></h2>
				<p><img src="images/morlanda_bi_297.jpg" style="float: left;" alt="">I have a confession to make but before I get to that I want to tell you a little bit about why I have a confession.  All my life I have been very interested in health and taking care of my body.  I was very involved in sports and exercise all through school but I never thought about making a career out of it.  In fact most people in my hometown, a town in Canada in the middle of nowhere, never went to school past high school.  Most of us worked on oil rigs right after school.  It was a decent job and you could make pretty good money.  I had my life all planned out until a met a woman.  Then everything got messed up.  Women can do that.  It was all for the better of course.  You see this woman was from the States and we got serious and decided to get married but she said she wouldn't live in Canada.  She said it was too beautiful in Canada and the people were too friendly.  Just kidding.  We did move to the States however.  That's when we decided I should go to chiropractic school.  That decision has changed my life forever.  You see while I was planning what I should do for the rest of my life, God had a better and bigger plan for me.  Since then I have learned to let go, and let God.  I graduated chiropractic school in 2004, 7 years and 3 kids later.  We have opened up a wellness center in Chanhassen, MN and things are going great.  Everyday I can wake up knowing that I am doing what God has called me to do and I get to help God's kingdom regain and remain healthy.  Now to my confession.  I must confess that I am not the one doing the healing.  It is the power that God put in everyone of us that allows us to heal.  Since our doors have opened though we have seen so many miracles and amazing things happen through God's healing.  We would love to see these things happen in your life as well.  Phone us today.  IT WILL CHANGE YOUR LIFE.!!!!!!!!</p>
				<p>Dr. Morland currently lives in Chaska, MN with his wife Natalie, twin daughters Brittany, Brianna, and son Nicholas.  He graduated Summa Cum Laude from Northwestern Chiropractic College.  He has special training in Gonstead technique and Torque Release Technique.  He also has specialized training in pediatrics and wellness care.  He is very active in the community, church and home.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Leah Shirley, CMT, EDST</strong></h2>
				<p><img src="images/leah0607.jpg" style="float: right;" alt="">I have been doing massage therapy since 1999. I graduated from
Minneapolis School of Massage and Bodywork, Inc. I love working on and
learning about the human body and I am enthralled by its functions and
the potential the body has to heal itself when given the right
materials. Massage therapy has so many positive effects on the body, and
I love being able to help facilitate wellness with my clients. Seeing
first hand how massage has given such great rewards to me personally
(click the "my story" link for how massage and CST has helped me
personally), as well as my clients encourages me to continue my pursuit
and growth of different massage techniques, so that I can offer my best
to you as your therapist. It is a thrill for me to learn and continue to
grow in the area of massage and bodywork.</p>

<p>I believe much of our illness and bodily dysfunction today is due to the
food we eat, the extreme stress and burdens we carry and the lack of
exercise. Our true freedom from these negative habits comes only through
Jesus Christ; however, if I can aid in your rest, relaxation and
elimination of negative factors that hinder you from being healed, as
well as encourage you through life's journey, I view it a privilege.</p>

<p>The massage modalities I use are relaxation, deep tissue, CranioSacral
Therapy, and Myofascial Release. My areas of specialty are headache /
migraine, TMJ , neck pain and pre/post natal. I continue to take
advanced training in CranioSacral therapy and am currently working on my
certification with The Upledger Institute. I also take continuing
education in Myofascial Release through John Barnes. In addition to
keeping my massage and bodywork current, I am working on my masters
degree in holistic nutrition.</p>

<p>My vision is to create a safe and comfortable environment of relaxation
and care to promote healing and wellness from within, while encouraging
my clients to take control of their health resulting in a rich and
enjoyable quality of life in a non-threatening way.</p>
			    </div>

			    <div id="services-wide">
				<h2><strong>Christine Stueve, Naturopath, EDST</strong></h2>
				<p><img src="images/christine.png" style="float: left;" alt="">I am a mom of four boys, wife and have a passion for God's word.  I am currently studying for my masters and doctorate degree in Naturopathy.  I am certified in Electro-Dermal Scanning (EDST), 1st Degree Black Belt in Tae Kwon Do Karate, Group Fitness Instruction, and CPR.  I am trained in CranioSacral Therapy (CST), Nutrition Response Testing (NRT), Functional Endocrinology and Hair Analysis.  Glycobiology is another of my educational pursuits and I currently volunteer as a Theophostic Prayer partner.  I have a deep passion to see men, women and children walk in divine health emotionally, mentally, physically & spiritually.  The more I study the body, cellular communication and God's incredible design, I realize how much more there is to learn.  I absolutely love what I do - walking alongside those who seek greater health and wellness.  </p>

<p>I am frequently invited to teach and train at churches, universities, Christian and public schools to educate on a variety of topics such as Nutrition, Emotions and their affects on health, Self Defense for Women, and Kickboxing.  Producing a Self Defense for Women DVD and guest speaking on KTIS were truly highlights of 2006.  </p>

<p>I am excited to see positive life change, personal growth, and a true sense of what it means to have energy and enjoy life in my clients - I can't wait to serve you!</p>
			    </div>
			</div><!-- div#serviceswhere -->
		    </div><!-- div#today -->
		</div><!-- div#todaywrapper -->
<%@ include file="channels\channel-menu-new.jsp" %>
	    </div><!-- div#bodyline -->
	    <div id="footline"><img src="images/footer.gif" alt="footer">
	    </div><!-- div#footline -->
	</div><!-- div#main -->

    </body>
</html>