<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
NewsItemBean content_index_page = null;
try
{
	content_index_page = NewsItemBean.getNewsItemBean(1);
}
catch (ObjectNotFoundException x)
{
	content_index_page = new NewsItemBean();
	content_index_page.setName("index");
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Stop Smoking Program Eden Prairie - Chanhassen</title>

<meta name="description" content="Valeo Wellness Center in Eden Prairie offers Athlete's Power Tonic, a herbal formula provides rapid recovery from strenuous physical stress. Our Eden Prairie, MN office is open Monday - Friday, starting at 7 a.m.">
<meta name="keywords" content="Stop Smoking, chiropractor, Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy,natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">

<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>
<script language="JavaScript" type="text/javascript" src="js/google-analytics.js"></script>


 
<style type="text/css"> 
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
</style> 
 
<link type="text/css" rel="stylesheet" href="http://yui.yahooapis.com/3.3.0/build/cssfonts/fonts-min.css" /> 
<script type="text/javascript" src="http://yui.yahooapis.com/3.3.0/build/yui/yui-min.js"></script> 
 
 
<!--begin custom header content for this example--> 
<style id="yui3-style-overrides"> 
 
#main #example-canvas .yui3-tabview .yui3-tab-selected a {
	color:white;
}
 
</style> 
<!--end custom header content for this example--> 
 

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-7869165-5']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

</head>
<body class="yui3-skin-sam  yui-skin-sam">
<div class="container">
  <div id="header">
    <div id="logo"></div>
    <!-- end #logo -->
    <div class="imagecycle">
      <div id="imageslider">
        <div><img src="img/Image_Cycle/Image_1.gif" alt="Valeo Health &amp; Wellness Image 1" /></div>
        <div><img src="img/Image_Cycle/Image_2.gif" alt="Valeo Health &amp; Wellness Image 2" /></div>
        <div><img src="img/Image_Cycle/Image_3.gif" alt="Valeo Health &amp; Wellness Image 3" /></div>
      </div>
      <!-- end #imageslider -->
    </div>
    <!-- end .imagecycle -->
    <div class="clear"></div>
    <!-- end .clear -->
    <div id="tagline"></div>
    <!-- end #tagline -->
  </div>
  <!-- end #header -->
<%@ include file="channels\channel-nav.jsp" %>
  <div id="floatwrapper">
    <div id="content">
     <h1>Stop Smoking Naturally!</h1><h2>You can stop smoking in just <span style="color: red;" >one week!</span></h2>
		<div ><img src="images/stop-smoking.png" alt="Stop Smoking" style="float: left; padding: 10px;" />
		<h2><font style="font-size: larger;" >Price: $619.00 for Stop Smoking Program</font></h2>
		<p>The "Just Stop" smoking cessation program includes the "Just Stop Too!" homeopathic smoking cessation formula. The "Just Stop" supportive herbal dietary supplement to promote calming of the body and temporarily reduce anxiety while using this program, as well as behavioral modification recommendations. Please carefully read all information regarding this program.<br /></p>
      </div>
      <!-- end #video --><br />
<%

%>
    </div>
    <!-- end #content -->
    <div id="sidebar">

    <!-- BEGIN: Constant Contact Stylish Email Newsletter Form -->

    
	<h1>Survey</h1>
      <p>Please take our online survey to help us better understand better how we can help you stop: </p>
	  <p><a href="http://survey.constantcontact.com/survey/a07e3ukpayegnizydn9/start" target="_blank" >Click here to take our survey.</a></p>
      
	  
	<h1>Visit Forms</h1>
      <p>Please take a moment to fill out these forms prior to your first visit: </p>
	  <p><a href="forms.jsp" >Click here to download the forms.</a></p>
	  
      
	  
     
    </div>
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
	
 
<div id="demo"> 
    <ul> 
        <li style="font-size: large; " ><a href="#foo">Overview</a></li> 
        <li style="font-size: large; "><a href="#baz">FAQ</a></li> 
        <li style="font-size: large; "><a href="#res">Resources</a></li> 
        <li style="font-size: large; "><a href="#bax">Testimonials</a></li> 
        <li style="font-size: large; "><a href="#nd">Email questions to a Doctor</a></li> 
        <li style="font-size: large; "><a href="#vi">Instructions</a></li> 
    </ul> 
    <div> 
        <div id="foo"> 
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;" >
				<li>The "Just Stop" smoking cessation program includes three laser treatments, supplement formula and homeopathic synergistically working together to promote calming of the body and temporarily reduce anxiety while using this program as well as behavioral modification recommendations. Please review the information on this site thoroughly prior to beginning the program.</li>
				<li>Do not use any alcohol, tobacco or products containing nicotine while using the "Just Stop" herbal dietary supplement.</li>
				<li>DRINK A MINIMUM OF 8 TO 12 GLASSES OF WATER (8 OZ) PER DAY WHILE USING THE "JUST STOP" HERBAL DIETARY SUPPLEMENT.</li>
				<li>If pregnant, nursing or taking any medications, consult with your physician prior to use. Discontinue use and consult with or physician if any adverse reactions occur.</li>
				<li>Keep out of reach from children.</li>
				<li>The "Just Stop" supportive herbal dietary supplement is designed to temporarily reduce or "Just Stop" irritability, anxiety and nervousness while helping to calm the body and more effectively deal with stress.</li>
				<li>Modifies craving for tobacco smoke or chew, even for ones who may feel better smoking. Helps prevent nervousness, anxiety and irritability wen quitting. Helps overcome the negative effects of smoking such as cough, shortness of breath, difficulty breathing and sensation of weight on the chest.</li>
				<li>Keep away from heat or sunlight.</li>
				<li>Take homeopathic supplement between meals.</li>
				<li>Products containing camphor, caffeine, chocolate, nicotine, raw garlic, menthol or mint should be avoided, especially one to two hours before and after taking homeopathic formulas.</li>
				<li>Homeopathy may induce flu-like symptoms. If that happens, cut your dosage in half and then gently increase intake back to the full dosage over a one week period.</li>
				<li>Homeopathy will not interfere with prescription medications.</li>
				<li>Do not interrupt pharmaceutical therapy without consulting your physician.</li>
			</ul>
        </div> 
        <div id="baz"> 
            <p><strong>Q: What types of health care practitioners use the BAX-3000?</strong></p> 
            <p>A: Because the BAX-3000 is a non-invasive and painless therapy it falls under the scope of practice for many types of physicians, including but not limited to Medical Doctors (MD), Doctors of Chiropractic (DC), Doctors of Osteopathy (DO), Homeopathic Doctors, Naturopathic Doctors and Acupuncturists.</p> 
            <p><strong>Q: Is the BAX-3000 safe for both the doctor and patient?</strong></p> 
            <p>A: Yes. In a very simple analogy, the BAX-3000 emits radio waves during an assessment which are essentially no different than listening to music. Our devices measure the patient's sensitivity to the music being played during an assessment which is no more harmful than listening to music with a friend at home or in your car.</p> 
            <p><strong>Q: Does insurance cover assessment and therapy?</strong></p> 
            <p>A: At this time, therapy and assessment are provided on a cash basis and are not covered by insurance. This is however, a very common practice among holistic and alternative medical practitioners that provide treatments of many varieties that fall beyond standard medical procedures covered by insurance companies.</p> 
            <p><strong>Q: Why would I pay cash for this service?</strong></p> 
            <p>A: As a person that has suffered from chronic conditions such as allergies, asthma, eczema or chronic fatigue syndrome you know how hard it can be to deal with the daily symptoms. As a parent with a child who suffers from these and other stress related conditions, its devastating to feel as though you are helpless to help them. When you hear stories from other patients that have achieved sometimes amazing symptomatic relief after years of failed, expensive, time consuming and painful traditional treatments its easy to see value in Neurological Stress Reduction Therapy.</p> 
            <p><strong>Q: What does a typical full course of therapy cost?</strong></p> 
            <p>A: Please talk to our doctor's or office staff for information about fees.</p> 
            <p><strong>Q: How many visits are needed?</strong></p> 
            <p>A: The number of visits required depends on the patient, the extent or severity of their conditions and their state of overall health and well being. The BAX 3000 therapy is comprised of four (4) primary procedures. For mild cases, a patient may need 7-10 visits and experience symptom relief after perhaps 1-2 visits. The first procedure may be sufficient to provide symptom relief. For more severe cases, a patient may need several dozen therapy sessions and require therapy for all four procedures in addition to advanced protocols.</p> 
            <p><strong>Q: How long is a typical doctors appointment?</strong></p> 
            <p>A: A typical appointment following the initial consultation visit will take an average of about 1 minutes with less than 10 minutes of hands on time.</p> 
            <p><strong>Q: When do patients notice improvement in their allergy symptoms?</strong></p> 
            <p>A: Many people notice diminishing symptoms after their first or second visit. Individual timetables to achieve symptomatic relief will vary based on the individual's severity of conditions and state of overall health.</p> 
            <p><strong>Q: What will I feel?</strong></p> 
            <p>A: A small percentage of patients report slight flushing or congestion for a short time (an hour or so) after their session, but this is actually a sign that the body is detoxifying (a good thing)! This process is safe, fast, non-invasive, painless and exposes the body to nothing more than sound and light.</p> 
            <p><strong>Q: Is this therapy and specifically the BAX-3000 safe for children?</strong></p> 
            <p>A: Yes, the BAX-3000 is safe for people of all ages.</p> 
            <p><strong>Q: What substances can the BAX-3000 identify as stressors?</strong></p> 
            <p>A: These devices contain several thousand substances in the main procedure libraries and up to an additional 96,000 substances in the advanced procedure libraries. This technology can identify almost every known substance that could possibly cause a stress reaction. The BAX-3000 contains the most comprehensive substance libraries of any devices of this type.</p> 
            <p><strong>Q: How does the BAX-3000 Allergen database compare to other treatments?</strong></p> 
            <p>A: The BAX-3000 uses a patented frequency capture/exposure method. No other company can offer our methodology or technology, it's patented.</p> 
            <p><strong>Q: What does the BAX-3000 do?</strong></p> 
            <p>A: The BAX-3000 offers Neurological Stress Relief Therapy or NSRT. Through the identification of specific substances that cause stress on the nervous system and he use of LASER (Light and Sound Energy Relaxation) Therapy the BAX-3000 is able to eliminate the nervous system triggers that cause numerous chronic and acute conditions.</p> 
            <p><strong>Q: What does the BAX-3000 treat?</strong></p> 
            <p>A: The BAX-3000 does not diagnose or treat any specific condition.</p> 
            <p>The science is based on the fact that specific stress-inducing substances are often times what trigger the nervous systems fight or flight reactions. These reactions are expressed in a myriad of symptoms that include skin irritations, headaches, sinus conditions, digestive discomfort and respiratory problems. These are the sypmtoms associated with chronic and acute conditions such as allergies, asthma, eczema, Rosacea, CFS (Chronic Fatigue Syndrome), headaches, migraines and IBS (Irritable Bowel Syndrome).</p> 
            <p>"According to The American Medical Association (AMA), stress is the cause of 80 to 85 percent of all human illness and disease. Every week, 95 million Americans suffer some kind of stress-related symptom for which they take medication."</p> 
            <p>Our belief is that through the use of our patented technology, the BAX-3000 can safely, effectively and quickly reduce stress which will therefore reduce or eliminate a number of chronic and acute conditions that are triggered by stress on the nervous system.</p> 
            <p><strong>Q: What is LASER Therapy?</strong></p> 
            <p>A: LASER Therapy is BioVeda Technologies patented approach to substance specific stress reductio. LASER stands for "Light And Sound Energy Relaxation". LASER Therapy is realized through the use of the DCM or biofeedback device which emits sound and the LCM or laser that emits a digital signal in the form of light.</p> 
            <p>BioVeda Technologies and their affiliated BioVeda Health and Wellness Centers exclusively utilize this patented and FDA approved approach to stress reduction therapy.</p> 
            <p><strong>Q: What does the DCM (Digital Conductance Meter) do?</strong></p> 
            <p>A: The DCM or Digital Conductance Meter converts the frequency substances stored on the computer into radio waves, presents each substance to the body in the form of a radio wave and records the body's electrical impedance to each specific substance back to the computer. The DCM is FDA approved and is considered a biofeedback device.</p> 
            <p><strong>Q: What does the LCM (Light Conductance Meter) do?</strong></p> 
            <p>A: The LCM or Light Conductance Meter converts the frequency substances stored on the computer into light, presents each substance to the body via a laser in the form of light and stimulates nerve bundles causing the brain to release endorphins and enkephalins. The release of the endorphins and enkephalins calm the body and create a new positive association with substances the brain previously believed were harmful or threatening.</p>  
		</div>
        <div id="res"> <br />
            <p><table cellpadding="3" cellspacing="3" border="2" style="border-style: solid; border-width: medium; " >
					<tr style="border-style: solid;  border-width: thin; "><td rowspan="2" ><font style="font-size: larger;" >Cigarettes<br /> Smoked<br /> Per Day</font></td><td colspan="6" align="center" ><font style="font-size: larger;" >Smoking Cost Per</font></td></tr>
					<tr><td>Day</td><td>Week</td><td>Month</td><td><b>Year</b></td><td>5 Years</td><td>10 Years</td></tr>
					<tr><td align="center">5</td><td align="right">&nbsp;$1.50&nbsp;</td><td align="right">&nbsp;$10.50&nbsp;</td><td align="right">&nbsp;$45.50&nbsp;</td><td align="right">&nbsp;$547.50&nbsp;</td><td align="right">&nbsp;$2,737.50&nbsp;</td><td align="right">&nbsp;$5,475.00&nbsp;</td></tr>
					<tr><td align="center">10</td><td align="right">&nbsp;$3.00&nbsp;</td><td align="right">&nbsp;$21.00&nbsp;</td><td align="right">&nbsp;$91.00&nbsp;</td><td align="right">&nbsp;$1,092.00&nbsp;</td><td align="right">&nbsp;$5,460.00&nbsp;</td><td align="right">&nbsp;$10,920.00&nbsp;</td></tr>
					<tr><td align="center"><b>20</b></td><td align="right">&nbsp;$6.00&nbsp;</td><td align="right">&nbsp;$42.00&nbsp;</td><td align="right">&nbsp;$182.00&nbsp;</td><td align="right">&nbsp;<b>$2,184.00</b>&nbsp;</td><td align="right">&nbsp;$10,920.00&nbsp;</td><td align="right">&nbsp;$21,840.00&nbsp;</td></tr>
					<tr><td align="center">30</td><td align="right">&nbsp;$9.00&nbsp;</td><td align="right">&nbsp;$63.00&nbsp;</td><td align="right">&nbsp;$273.00&nbsp;</td><td align="right">&nbsp;$3,276.00&nbsp;</td><td align="right">&nbsp;$16,380.00&nbsp;</td><td align="right">&nbsp;$32,760.00&nbsp;</td></tr>
					<tr><td align="center">40</td><td align="right">&nbsp;$12.00&nbsp;</td><td align="right">&nbsp;$84.00&nbsp;</td><td align="right">&nbsp;$364.00&nbsp;</td><td align="right">&nbsp;$4,368.00&nbsp;</td><td align="right">&nbsp;$21,840.00&nbsp;</td><td align="right">&nbsp;$43,680.00&nbsp;</td></tr>
					<tr><td align="center">50</td><td align="right">&nbsp;$18.00&nbsp;</td><td align="right">&nbsp;$126.00&nbsp;</td><td align="right">&nbsp;$546.00&nbsp;</td><td align="right">&nbsp;$6,552.00&nbsp;</td><td align="right">&nbsp;$32,760.00&nbsp;</td><td align="right">&nbsp;$65,520.00&nbsp;</td></tr>
					
				</table><font style="font-size: smaller;" >Based on a cost of $6.00 per pack of cigarettes.</font></p>
				<p>&nbsp;</p>
			<p><h2>The Effects of Smoking</h2></p>
			<p>If you area smoker, then surely you are already aware of the danger that you are putting yourself in every time you have a cigarette. Heart disease and cancer are the most hazardous effects of smoking, but there are a slew of other conditions that affect smokers on a daily basis. We won't rehash these detrimental effects here, but we will explain how quitting smoking adds years to your life:</p>
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;">
				<li>Within 20 minutes after you smoke that last cigarette, your body begins a series of changes that continue for years;</li>
				<li>12 hours after quitting, carbon monoxide levels in your blood drop to normal;</li>
				<li>After 24 hours, your lungs begin to clear out accumulated mucous and tar. Your pulse rate and blood pressure begin to lower;</li>
				<li>After 48 hours, your sense of smell and taste begin to improve as nicotine is eliminated from your body;</li>
				<li>After 72 hours, your bronchial tubes begin to relax, making breathing easier and lung capacity increases;</li>
				<li>After 2 to 12 weeks, circulation improves, making walking and physical activity easier;</li>
				<li>After 2 weeks to 3 months, you heart attack risk begins to drop and your lung function begins to improve;</li>
				<li>One to 9 months after quitting, coughing and sinus congestion decreases. Lung function improves, shortness of breath decreases, energy level increases;</li>
				<li>1 year after quitting, your added risk of coronary heart disease is half that of a smoker;</li>
				<li>10 years after quitting, your lung cancer death rate is about half that of a smoker. Your risks of cancer of the mouth, throat, esophagus bladder, kidney and pancreas decrease;</li>
				<li>15 years after quitting, your risk of coronary heart disease and lung cancer is back to that of someone who has never smoked.</li>
			</ul>
				<p>&nbsp;</p>
			<p><h2>Other Important Facts About Smoking</h2></p>
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;">
				<li>Tobacco smoke contains over 4,000 different chemicals. At least 50 are known carcinogens (cause cancer in humans) and many are poisonous;</li>
				<li>Tobacco kills up to half of its regular users;</li>
				<li>Tobacco caused 100 million deaths in the 20th century;</li>
				<li>Cigarettes are one of few products which can be sold legally which can harm and even kill you over time if used as intended;</li>
				<li>Scientists claim the average smoker will lose 14 years of their life due to smoking;</li>
				<li>Nicotine reaches the brain within 10 seconds after smoke is inhaled. It has been found in every part of the body and in breast milk;</li>
				<li>Sugar approximates to roughly 20% of a cigarette, and many diabetics are unaware of this secret sugar intake. Also, the effect of burning sugar is unknown;</li>
				<li>'Lite' cigarettes are produced by infusing tobacco with CO2 and super-heating it until the tobacco 'puffs up' like expanding foam. The expanded tobacco then fills the same paper tube as 'regular' tobacco;</li>
				<li>Smokers draw on 'lite' and menthol cigarettes harder (on average) than regular cigarettes; causing the same overall levels of tar and nicotine to be consumed;</li>
				<li>Several active ingredients and special methods of production are involved in making sure the nicotine in a cigarette is many times more potent than that of a tobacco plant.</li>
			</ul>
				<p>&nbsp;</p>
			<p><h2>Homeopathy Background & Reference</h2></p>
			<p>The man known as the father of homeopathy was Dr. Samuel Hahnemann, a German physician. During the late eighteenth century. Hahnemann rediscovered and redefined some basic principles regarding the nature of health and illness. For the last two centuries, Hahnemann's philosophy and methods have withstood the test of time and have proven to be highly effective.</p>
			
				<p>&nbsp;</p>
			<p><h2>Homeopathy & Self Healing</h2></p>
			<p>Homeopathic remedies utilize minute amounts of natural substances to stimulate the body's self-healing process; the body's resources are used to resolve symptoms rather than suppress them. Homeopathic treatment seeks to mobilize the innate healing powers of the individual so that all physiological systems function at their best and the body's innate curative powers are activated.</p>
			
			<p>Following Hahnemann's death in 1843, other homeopathic physicians elaborated and refined his discoveries. Hahnemann's legacy can be summed up in the phrase similar 'similibus curentur' ~ let likes be treated by likes ~ recognizing that symptoms are an effort by the body to heal itself. Homeopathy sees symptoms as the body's natural response in fighting illness. Consequently, symptoms are utilized as a source of information to guide the selection of the most appropriate remedies for a persons specific ailments.</p>
			
			<p>Conventional allopathic medicine perceives symptoms to be the result of illness and proceeds on the principle that a disease or symptom is cured by using a medicine that opposes the symptom. Often, this entails direct suppression of the symptom. The predominant allopathic philosophy is contraria contraries ~ use medicines to stop the symptoms.</p>
			
				<p>&nbsp;</p>
			<p><h2>Homeopathy Today</h2></p>
			<p>Homeopathy enjoys widespread support in the United States, Europe and Canada. After virtually disappearing from the forefront of medicine in the early twentieth century, it is once again rapidly gaining popularity. Currently, American universities such as UCLA and the University of Washington are researching the effects of homeopathy. Courses using homeopathic formulations are being taught at Harvard Medical School, Tufts and Duke Universities. German physicians have developed advanced techniques in homotoxicology and immunotoxicology. Homotoxicology, primarily through the use of homeopathy, helps the body eliminate toxic stress responses to antibiotics, x-rays, metals and other substances that create stress and illness within the body.</p>
			
			<p>Homeopathy works effectively with a wide variety of acute and chronic problems, including infections, allergies, gynecological difficulties and digestive problems. Homeopathy also helps to prevent future problems by increasing the individual's immunological strength and resistance which is normally lowered due to the stresses of everyday living.</p>
			
				<p>&nbsp;</p>
			<p><h2>Homeopathic Medicine</h2></p>
			<p>Homeopathic Medications are prepare according to the Pharmacopoeia and guidelines set forth by the FDA. Homeopathy is classified as GRAS, Generally Regarded as Safe. More than half of all homeopathic medicines are derived from plants. For example, Arnica, St. John's Wort, Marigold and Rosemary have become common homeopathic medicines. The animal kingdom provides substances such as hormones, venoms and physiological secretions. Forms of homeopathic remedies include pellets, tablets, dilutions, ointments and suppositories.</p>
			
				<p>&nbsp;</p>
			<p><h2>Homeopathic Pharmacopoeia & Assurance of Quality</h2></p>
			<p>Look for the initials "HPUS" after the drug names on all of your homeopathic medicines. "HPUS" means your product is prepared according to the standards of the Homeopathic Pharmacopoeia of the United States and is your assurance of a quality product.</p>
			
			
        </div> 
        <div id="bax"> 
            <p>I recently was able to have the BAX treatment at Valeo to help quit smoking.  I started smoking when I was 18 years old and was still smoking a pack a day 16 years later.  I'll start by saying I'd tried almost everything before to quit smoking, including Zyban, Chantix, and the patch.  Each of these had side effects that were not very desirable.  Some made me depressed and irritable.  Trying to quit smoking all of these previous times was mostly just a miserable experience.</p> 
			<p>When Dr. Rob and Christine suggested the BAX treatment I was excited, skeptical and terrified all at the same time.  It sounded so simple, when my previous experience told me this was not an easy thing!  I just kept thinking about all the times I'd failed and struggled through this process before.  But, I was ready to give it a try!</p>
			<p>And I am so glad that I did try!  I showed up for the first of three BAX treatments and 15 minutes later I walked out a non-smoker.  I was told to throw my cigarettes away as I wouldn't be needing them.  I can tell you that throwing them out and going on my way gave me a moment of panic.  But, 30 minutes later, 2 hours later, 1 week later and a month later I still hadn't felt the need to pick one up!  It feels like I just walked away from all those years of smoking.  I didn't have to struggle through each moment of every day to do it either.  The only thing I felt was some night sweats and trouble sleeping for the first week.  After that, I just felt free!  Free of the addiction that used to have such control over so many things in my life.</p>
			<p>I am so grateful to everyone at Valeo!</p>
			<p><h2>Becky J</h2></p>
			<p>&nbsp;</p>
            <p>I was a smoker for 30 years and hadn't planned to quit until I developed a serious health condition that scared me enough to quit cold turkey 2 years ago. I didn't smoke for a year following surgery but the urge/desire to smoke never really left me. Slowly the urge started to take over again and I started and stopped occasional smoking multiple times - I really didn't want to smoke anymore but the urge was too powerful during very stressful times at work.</p> 
			<p>When I heard the Stop Smoking BAX program would actually take away the urge to smoke, I had to try it - I really wanted to never smoke again. The program was very easy - take a few supplements for a short period of time and have 3 ten minute treatments in one week. That's it! I wanted it to work but I was really skeptical.</p>
			<p>After the 1st treatment I could tell I didn't need nicotine anymore. I was relaxed and handled the pressure at work with no urges at all. The need to satisfy the nicotine craving is gone. I can take a deep breath and I am so much more relaxed. I don't have to fight it off anymore - I literally am free of that addiction thanks to Valeo's Stop Smoking Program.</p>
			<p><h2>Steve C</h2></p>
        </div> 
        <div id="nd"> 
            <p>

					<struts-html:form action="/emailDoc" onsubmit="return validateLoginForm(this);" focus="email">

						<input id="dummy" name="dummy" type="hidden" />

						<div class="loginItem">
							<div class="left">PROVIDE YOUR EMAIL ADDRESS:</div>
							<div class="right"><input name="email" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left">EMAIL SUBJECT:</div>
							<div class="right"><input name="subject" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left">EMAIL TEXT:</div>
							<div class="right"><textarea name="text" rows="5"  class="inputbox" style="width: 412px;" ></textarea></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Send Email" alt="Send Email" /></div>
							<div class="end"></div>
						</div>

					</struts-html:form></p> 
        </div> 
        <div id="vi"> 
            <p><h2>Directions for Use: ("Just Stop" Dietary Supplement Capsules)</h2></p>  
            
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;">
				<li>On the FIRST DAY, take only (4) "Just Stop" capsules 30-60 minutes prior to bed with a full 8 oz glass of water;</li>
				<li>Continue taking (4) capsules (3) times per day in the morning, afternoon and evening for (7) consecutive days until all capsules have been consumed;</li>
				<li>Always take capsules with a full 8 oz glass of water;</li>
				<li>Take "Just Stop" capsules one hour before or after taking any medications or taking any other nutritional supplements;</li>
				<li>"Just Stop" capsules can be taken with meals or in between meals;</li>
				<li>Do not take "Just Stop" capsules with milk;</li>
				<li>Discard all tobacco products prior to beginning the "Just Stop" program.</li>
			</ul>
			<br />
            <p><h2>Directions for Use: ("Just Stop Too!" Homeopathic Spray)</h2></p>  
            
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;">
				<li>Depress pump until primed prior to first use. Hold close to mouth and spray 1 dose directly in mouth 2 to 6 times daily until symptoms improve. 1 dose may also be sprayed into the air and inhaled through the nose. For best results spray in mouth directly under the tongue and hold for 30 seconds prior to swallowing;</li>
				<li>WEEK 1: Use (3) sprays (3) times per day for (7) consecutive days in between consumption or "Just Stop" herbal dietary supplement;</li>
				<li>WEEK 2-4: Use (3) sprays (6) times per day for (21) consecutive days to help manage cravings, irritability, nervousness and anxiety;</li>
				<li>Maintenance: Use 3) sprays (3) times per day OR as cravings for nicotine arise for up to (5) months to help manage cough, shortness of breath, anxiety, cravings, nervousness and irritability related o smoking cessation;</li>
				<li>Additional Usage: Use homeopathy any time cravings present as a replacement for smoking activity but not to exceed (6) uses per day;</li>
				<li>Do not use any tobacco or products containing nicotine while attempting to stop smoking;</li>
				<li>If pregnant, nursing or taking any medications, consult with your physician prior to use;</li>
				<li>Discontinue use and consult with your physician if any adverse reactions occur;</li>
				<li>Keep out of reach from children.</li>
			</ul></ul>
			<br />
            <p><h2>Consumer Instructions & Behavioral Considerations</h2></p>  
            
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;">
				<li>You must be committed and truly desire to quit smoking ~ not for someone else, but for yourself;</li>
				<li>Ask yourself the following questions to determine if you're ready to quit;</li>
				<li>Do you spend more than $100 a month on smoking? (See chart in "Resources" section)</li>
				<li>How else can you apply that money constructively and do you find the expense to be a financial burden?</li>
				<li>Do you feel that smoking controls or interferes in your life?</li>
				<li>Do you hide the fact that you smoke from family, friends and co-workers or find embarrassment in being a smoker?</li>
				<li>Which of these fears do you have of stopping smoking?  Weight Gain; Withdrawal/Anxiety; Stress Reduction Crutch; None?</li>
				<li>Do you or other members of your family smoke? If yes, do you believe you can successfully quit in an environment where others will continue to smoke?</li>
				<li>Do you have a smoking related illness?</li>
				<li>How many cigarettes per day do you smoke?</li>
				<li>How long have you been smoking?</li>
				<li>On a scale of 1 to 10, is your desire to quit smoking at least an 8, 9  or 10?</li>
				<li>Have you attempted to quit smoking in the past?</li>
				<li>Have you failed in the past?</li>
				<li>Why have you not been able to quit before and why will you be more successful now?</li>
				<li>If you are committed, look for alternative behaviors to replace habits learned and reinforced over many years such as chewing gum, sunflower seeds, a straw, a toothpick or a handful of trail mix. Replace these activities at times you associate smoking such as when driving, talking on the phone, breaks at work and before or after meals;</li>
				<li>Behavior modification techniques (8) ~ these are from the International Coalition Against Nicotine (ICAN) ~ because of the behavioral aspects of your addiction, it is likely that you may think about having a cigarette after using the "Just Stop" smoking cessation program. This is normal. However, because "Just Stop" has eliminated or greatly reduced your physical withdrawal symptoms, saying "No" to your past behaviors will be much easier:</li>
				<li>1. Take Action ~ Avoid the break areas at work, where tobacco users frequent. Avoid parties where alcohol and tobacco are present. Doing this will initially allow you to become comfortable as a non-smoker.</li>
				<li>2. Distract Yourself ~ When you think about having a cigarette or taking a dip or chew of tobacco, distract yourself from your thought. Talk to someone, go for a walk, get busy with a task or take deep cleansing breaths. If possible, get up and move around. Get a drink of water. You will be amazed at now quickly the thought will pass.</li>
				<li>3. Change Your Routine ~ To counter your mind's desire to act as you have always acted, alter your routine. You've associated nicotine use with pleasurable times in the past, and you want to have that comfort now in times of anxiety or stress. At ICAN, we understand that this happens. If you think about having a cigarette, think about it and then let it go! But if you think you can get away with just one puff, your're wrong. It will only lead you to finishing that cigarette then onto another an another. Before you know it, you are smoking again. Drive a different route to work. Eat breakfast in a different place. Literally, get up on the other side of the bed. The opportunities to make simple changes are nearly limitless and could help you to avoid falling back into old daily habits.</li>
				<li>4. Drink Lots of Water ~ Nicotine is a poison. Your body wants to eliminate it from your system and it does so very quickly. To help your body cleanse itself, drink lots of water and eat healthy foods such as fruit and nuts. It is strongly recommended that you drink at least 8 to 12 8 oz glasses of water daily especially while using the "Just Stop" products allowing the body to detoxify and cleanse itself of nicotine and smoking related toxins.</li>
				<li>5. No Alcohol ~ Do not consume alcohol until you have finished the entire (7) day dosage of the "Just Stop" herbal product. Alcohol can lower your inhibitions, making you think you can take just one puff or dip. Better to avoid the temptation all together. Further, alcohol is a toxin which you will be adding to your body during a time of purification and detoxification making it counter-productive to the effectiveness of the "Just Stop" program.</li>
				<li>6. Avoid Other Smokers ~ As with alcohol, until you have remained tobacco free for at least 4-6 weeks, avoid socializing with other who are smoking or using tobacco. Remember to tell your friends and family that you have quit and that you need their help and understanding. Make them a part of the process.</li>
				<li>7. Positive Thinking ~ During the day, quietly repeat to yourself, "I am a non-smoker." Many quitters see themselves as smokers who are just not smoking for the moment. They have a self-image as smokers who still want a cigarette. Silently repeating, "I am a non-smoker" will help you change your view of yourself, and even if it may seem silly to you, this is actually useful. Use it!</li>
				<li>8. Reward Yourself ~ During the first two weeks of your smoking cessation, do something nice for yourself on a daily basis. Take the money that you previously spent on nicotine and tobacco products and put it in a jar. Use this to treat yourself to something you have always wanted. You'll be amazed at how fast the money adds up!</li>
			</ul>
        </div> 
    </div> 
</div> 
<script type="text/javascript"> 
YUI({ filter: 'raw' }).use("yui", "tabview", function(Y) {
    var tabview = new Y.TabView({srcNode:'#demo'});
    tabview.render();
});
</script> 
	<br /><br />
	
	<!--
    <div class="boxesbottom">
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Directions for Use</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Product Info / FAQ</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Testimonials</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    </div>
	-->
	
    <!-- end .boxesbottom -->
  </div>
  <!-- end #floatwrapper -->
  <div class="clearfooter"> </div>
  <!-- end .clearfooter -->
</div>
<!-- end .container -->
<%@ include file="channels\channel-footer-new.jsp" %>
</body>
</html> 