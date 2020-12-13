/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.spider;

import com.badiyan.uk.online.beans.DiagnosisCodeIndexEntry;
import com.badiyan.uk.online.beans.DiagnosisCodeVersion;
import java.util.ArrayList;

/**
 *
 * @author marlo
 */
public class DiagnosisCodeMiner {
	
	private static final short NO_STATE = 0;
	
	private static final short LT = 1;
	private static final short LTS = 2;
	private static final short LTSP = 3;
	private static final short LTSPA = 4;
	private static final short LTSPAN = 5;
	private static final short LTSPAN_ = 6;
	private static final short LTSPAN_C = 7;
	private static final short LTSPAN_CL = 8;
	private static final short LTSPAN_CLA = 9;
	private static final short LTSPAN_CLAS = 10;
	private static final short LTSPAN_CLASS = 11;
	private static final short LTSPAN_CLASSEQ = 12;
	private static final short LTSPAN_CLASSEQQT = 13;
	
	private static final short LTSPAN_CLASSEQQTI = 14;
	private static final short LTSPAN_CLASSEQQTID = 15;
	private static final short LTSPAN_CLASSEQQTIDE = 16;
	private static final short LTSPAN_CLASSEQQTIDEN = 17;
	private static final short LTSPAN_CLASSEQQTIDENT = 18;
	private static final short LTSPAN_CLASSEQQTIDENTI = 19;
	private static final short LTSPAN_CLASSEQQTIDENTIF = 20;
	private static final short LTSPAN_CLASSEQQTIDENTIFI = 21;
	private static final short LTSPAN_CLASSEQQTIDENTIFIE = 22;
	private static final short LTSPAN_CLASSEQQTIDENTIFIER = 23;
	private static final short LTSPAN_CLASSEQQTIDENTIFIERQT = 24;
	private static final short LTSPAN_CLASSEQQTIDENTIFIERQTGT = 25;
	
	private static final short DIAGNOSIS_CODE = 26;
	
	// <div class="
	private static final short LTD = 27;
	private static final short LTDI = 28;
	private static final short LTDIV = 29;
	private static final short LTDIV_ = 30;
	private static final short LTDIV_C = 31;
	private static final short LTDIV_CL = 32;
	private static final short LTDIV_CLA = 33;
	private static final short LTDIV_CLAS = 34;
	private static final short LTDIV_CLASS = 35;
	private static final short LTDIV_CLASSEQ = 36;
	private static final short LTDIV_CLASSEQQT = 37;
	
	
	private static final short LTDIV_CLASSEQQTD = 38;
	private static final short LTDIV_CLASSEQQTDE = 39;
	private static final short LTDIV_CLASSEQQTDES = 40;
	private static final short LTDIV_CLASSEQQTDESC = 41;
	private static final short LTDIV_CLASSEQQTDESCR = 42;
	private static final short LTDIV_CLASSEQQTDESCRI = 43;
	private static final short LTDIV_CLASSEQQTDESCRIP = 44;
	private static final short LTDIV_CLASSEQQTDESCRIPT = 45;
	private static final short LTDIV_CLASSEQQTDESCRIPTI = 46;
	private static final short LTDIV_CLASSEQQTDESCRIPTIO = 47;
	private static final short LTDIV_CLASSEQQTDESCRIPTION = 48;
	private static final short LTDIV_CLASSEQQTDESCRIPTIONQT = 49;
	private static final short LTDIV_CLASSEQQTDESCRIPTIONQTGT = 50;
	
	private static final short DESCRIPTION = 51;
	
	
	// <div class="blurbSubsectionHeading">
	private static final short LTDIV_CLASSEQQTB = 52;
	private static final short LTDIV_CLASSEQQTBL = 53;
	private static final short LTDIV_CLASSEQQTBLU = 54;
	private static final short LTDIV_CLASSEQQTBLUR = 55;
	private static final short LTDIV_CLASSEQQTBLURB = 56;
	private static final short LTDIV_CLASSEQQTBLURBS = 57;
	private static final short LTDIV_CLASSEQQTBLURBSU = 58;
	private static final short LTDIV_CLASSEQQTBLURBSUB = 59;
	private static final short LTDIV_CLASSEQQTBLURBSUBS = 60;
	private static final short LTDIV_CLASSEQQTBLURBSUBSE = 61;
	private static final short LTDIV_CLASSEQQTBLURBSUBSEC = 62;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECT = 63;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTI = 64;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIO = 65;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTION = 66;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONH = 67;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHE = 68;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEA = 69;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEAD = 70;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEADI = 71;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEADIN = 72;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEADING = 73;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQT = 74;
	private static final short LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQTGT = 75;
	
	private static final short BLURB = 76;
	private static final short BLURBLT = 77;
	private static final short BLURBLTFS = 78;
	private static final short BLURBLTFSD = 79;
	private static final short BLURBLTFSDI = 80;
	private static final short BLURBLTFSDIV = 81;
	private static final short BLURBLTFSDIVGT = 82;
	
	// Definition(s)<img
	private static final short D = 83;
	private static final short DE = 84;
	private static final short DEF = 85;
	private static final short DEFI = 86;
	private static final short DEFIN = 87;
	private static final short DEFINI = 88;
	private static final short DEFINIT = 89;
	private static final short DEFINITI = 90;
	private static final short DEFINITIO = 91;
	private static final short DEFINITION = 92;
	private static final short DEFINITIONP = 93;
	private static final short DEFINITIONPS = 94;
	private static final short DEFINITIONPSP = 95;
	private static final short DEFINITIONPSPLT = 96;
	private static final short DEFINITIONPSPLTI = 97;
	private static final short DEFINITIONPSPLTIM = 98;
	private static final short DEFINITIONPSPLTIMG = 99;
	
	// Excludes <img
	private static final short E = 100;
	private static final short EX = 101;
	private static final short EXC = 102;
	private static final short EXCL = 103;
	private static final short EXCLU = 104;
	private static final short EXCLUD = 105;
	private static final short EXCLUDE = 106;
	private static final short EXCLUDES = 107;
	private static final short EXCLUDES_ = 108;
	private static final short EXCLUDES_LT = 109;
	private static final short EXCLUDES_LTI = 110;
	private static final short EXCLUDES_LTIM = 111;
	private static final short EXCLUDES_LTIMG = 112;
	
	// Applies To<img
	private static final short A = 113;
	private static final short AP = 114;
	private static final short APP = 115;
	private static final short APPL = 116;
	private static final short APPLI = 117;
	private static final short APPLIE = 118;
	private static final short APPLIES = 119;
	private static final short APPLIES_ = 120;
	private static final short APPLIES_T = 121;
	private static final short APPLIES_TO = 122;
	private static final short APPLIES_TOLT = 123;
	private static final short APPLIES_TOLTI = 124;
	private static final short APPLIES_TOLTIM = 125;
	private static final short APPLIES_TOLTIMG = 126;
	
	// converts directly to:
	private static final short C = 127;
	private static final short CO = 128;
	private static final short CON = 129;
	private static final short CONV = 130;
	private static final short CONVE = 131;
	private static final short CONVER = 132;
	private static final short CONVERT = 133;
	private static final short CONVERTS = 134;
	private static final short CONVERTS_ = 135;
	private static final short CONVERTS_D = 136;
	private static final short CONVERTS_DI = 137;
	private static final short CONVERTS_DIR = 138;
	private static final short CONVERTS_DIRE = 139;
	private static final short CONVERTS_DIREC = 140;
	private static final short CONVERTS_DIRECT = 141;
	private static final short CONVERTS_DIRECTL = 142;
	private static final short CONVERTS_DIRECTLY = 143;
	private static final short CONVERTS_DIRECTLY_ = 144;
	private static final short CONVERTS_DIRECTLY_T = 145;
	private static final short CONVERTS_DIRECTLY_TO = 146;
	private static final short CONVERTS_DIRECTLY_TOC = 147;
	
	//<div class="contentBlurb">
	
	private static final short LTDIV_CLASSEQQTC = 148;
	private static final short LTDIV_CLASSEQQTCO = 149;
	private static final short LTDIV_CLASSEQQTCON = 150;
	private static final short LTDIV_CLASSEQQTCONT = 151;
	private static final short LTDIV_CLASSEQQTCONTE = 152;
	private static final short LTDIV_CLASSEQQTCONTEN = 153;
	private static final short LTDIV_CLASSEQQTCONTENT = 154;
	private static final short LTDIV_CLASSEQQTCONTENTB = 155;
	private static final short LTDIV_CLASSEQQTCONTENTBL = 156;
	private static final short LTDIV_CLASSEQQTCONTENTBLU = 157;
	private static final short LTDIV_CLASSEQQTCONTENTBLUR = 158;
	private static final short LTDIV_CLASSEQQTCONTENTBLURB = 159;
	private static final short LTDIV_CLASSEQQTCONTENTBLURBQT = 160;
	private static final short LTDIV_CLASSEQQTCONTENTBLURBQTGT = 161;
	
	//back-references
	private static final short B = 162;
	private static final short BA = 163;
	private static final short BAC = 164;
	private static final short BACK = 165;
	private static final short BACKD = 166;
	private static final short BACKDR = 167;
	private static final short BACKDRE = 168;
	private static final short BACKDREF = 169;
	private static final short BACKDREFE = 170;
	private static final short BACKDREFER = 171;
	private static final short BACKDREFERE = 172;
	private static final short BACKDREFEREN = 173;
	private static final short BACKDREFERENC = 174;
	private static final short BACKDREFERENCE = 175;
	private static final short BACKDREFERENCES = 176;
	
	private static final short BACKDREFERENCESLT = 177;
	private static final short BACKDREFERENCESLTD = 178;
	private static final short BACKDREFERENCESLTDL = 179;
	private static final short BACKDREFERENCESLTDLGT = 180;
	
	private static final short BACKDREFERENCESLTDLGTLT = 181;
	private static final short BACKDREFERENCESLTDLGTLTFS = 182;
	private static final short BACKDREFERENCESLTDLGTLTFSD = 183;
	private static final short BACKDREFERENCESLTDLGTLTFSDL = 184;
	private static final short BACKDREFERENCESLTDLGTLTFSDLGT = 185;
	
	
	//<ul class="codeList">
	private static final short LTU = 186;
	private static final short LTUL = 187;
	private static final short LTUL_ = 188;
	private static final short LTUL_C = 189;
	private static final short LTUL_CL = 190;
	private static final short LTUL_CLA = 191;
	private static final short LTUL_CLAS = 192;
	private static final short LTUL_CLASS = 193;
	private static final short LTUL_CLASSEQ = 194;
	private static final short LTUL_CLASSEQQT = 195;
	private static final short LTUL_CLASSEQQTC = 196;
	private static final short LTUL_CLASSEQQTCO = 197;
	private static final short LTUL_CLASSEQQTCOD = 198;
	private static final short LTUL_CLASSEQQTCODE = 199;
	private static final short LTUL_CLASSEQQTCODEL = 200;
	private static final short LTUL_CLASSEQQTCODELI = 201;
	private static final short LTUL_CLASSEQQTCODELIS = 202;
	private static final short LTUL_CLASSEQQTCODELIST = 203;
	private static final short LTUL_CLASSEQQTCODELISTQT = 204;
	private static final short LTUL_CLASSEQQTCODELISTQTGT = 205;
	
	private static final short LTUL_CLASSEQQTCODELISTQTGTLT = 206;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTL = 207;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTLI = 208;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTLIGT = 209;
	
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFS = 210;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSU = 211;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSUL = 212;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSULGT = 213;
	
	
	
	
	//<ul class="codeList">
	/*
	private static final short LTU = 186;
	private static final short LTUL = 187;
	private static final short LTUL_ = 188;
	private static final short LTUL_C = 189;
	private static final short LTUL_CL = 190;
	
	*/
	
	

	private String mine_str;
	private short state = 0;
	
	
	private String diagnosis_code = "";
	private String description = "";
	private String diagnosis_definitions = "";
	private String excludes = "";
	private String applies_to = "";
	private String convert_to = "";
	private String content = "";


	private byte blurb_state = 0;
	private String blurb = "";
	private String back_references = "";
	
	ArrayList index_entries = new ArrayList();
	
	DiagnosisCodeVersion version = null;
	
	public DiagnosisCodeMiner(String _str) throws Exception {
		mine_str = _str;
		version = DiagnosisCodeVersion.getDiagnosisCodeVersion("2012 ICD-9-CM", true);
	}
	
	public void mine() {
		
		for (int i = 0; i < mine_str.length(); i++) {
			char current_char = mine_str.charAt(i);
			
			switch (state) {
				case NO_STATE: state = (current_char == '<' ? LT : current_char == 'D' ? D : current_char == 'E' ? E : current_char == 'A' ? A : current_char == 'c' ? C : current_char == 'b' ? B : NO_STATE); break;
				case DIAGNOSIS_CODE: state = (current_char == '<' ? NO_STATE : DIAGNOSIS_CODE); if (state == DIAGNOSIS_CODE) diagnosis_code += current_char; break;
				case DESCRIPTION: state = (current_char == '<' ? NO_STATE : DESCRIPTION); if (state == DESCRIPTION) description += current_char; break;
					
				case BLURB: state = (current_char == '<' ? BLURBLT : BLURB); blurb += current_char; break;
				case BLURBLT: state = (current_char == '/' ? BLURBLTFS : BLURB); blurb += current_char; break;
				case BLURBLTFS: state = (current_char == 'd' ? BLURBLTFSD : BLURB); blurb += current_char; break;
				case BLURBLTFSD: state = (current_char == 'i' ? BLURBLTFSDI : BLURB); blurb += current_char; break;
				case BLURBLTFSDI: state = (current_char == 'v' ? BLURBLTFSDIV : BLURB); blurb += current_char; break;
				case BLURBLTFSDIV: state = (current_char == '>' ? BLURBLTFSDIVGT : BLURB); blurb += current_char; break;
				case BLURBLTFSDIVGT: {
					switch (blurb_state) {
						case 1: diagnosis_definitions = blurb.substring(0, blurb.length() - 6); break;
						case 2: excludes = blurb.substring(0, blurb.length() - 6); break;
						case 3: applies_to = blurb.substring(0, blurb.length() - 6); break;
						case 4: convert_to = blurb.substring(0, blurb.length() - 6); break;
						case 5: content = blurb.substring(0, blurb.length() - 6); break;
					}
					blurb = "";
					state = NO_STATE;
					break;
				}
					
				case LT: state = (current_char == 's' ? LTS : current_char == 'd' ? LTD : current_char == 'u' ? LTU : NO_STATE); break;
				case LTS: state = (current_char == 'p' ? LTSP : NO_STATE); break;
				case LTSP: state = (current_char == 'a' ? LTSPA : NO_STATE); break;
				case LTSPA: state = (current_char == 'n' ? LTSPAN : NO_STATE); break;
				case LTSPAN: state = (current_char == ' ' ? LTSPAN_ : NO_STATE); break;
				case LTSPAN_: state = (current_char == 'c' ? LTSPAN_C : current_char == ' ' ? LTSPAN_ : NO_STATE); break;
				case LTSPAN_C: state = (current_char == 'l' ? LTSPAN_CL : NO_STATE); break;
				case LTSPAN_CL: state = (current_char == 'a' ? LTSPAN_CLA : NO_STATE); break;
				case LTSPAN_CLA: state = (current_char == 's' ? LTSPAN_CLAS : NO_STATE); break;
				case LTSPAN_CLAS: state = (current_char == 's' ? LTSPAN_CLASS : NO_STATE); break;
				case LTSPAN_CLASS: state = (current_char == '=' ? LTSPAN_CLASSEQ : current_char == ' ' ? LTSPAN_CLASS : NO_STATE); break;
				case LTSPAN_CLASSEQ: state = (current_char == '"' ? LTSPAN_CLASSEQQT : current_char == ' ' ? LTSPAN_CLASSEQ : NO_STATE); break;
					
				case LTD: state = (current_char == 'i' ? LTDI : NO_STATE); break;
				case LTDI: state = (current_char == 'v' ? LTDIV : NO_STATE); break;
				case LTDIV: state = (current_char == ' ' ? LTDIV_ : NO_STATE); break;
				case LTDIV_: state = (current_char == 'c' ? LTDIV_C : current_char == ' ' ? LTDIV_ : NO_STATE); break;
				case LTDIV_C: state = (current_char == 'l' ? LTDIV_CL : NO_STATE); break;
				case LTDIV_CL: state = (current_char == 'a' ? LTDIV_CLA : NO_STATE); break;
				case LTDIV_CLA: state = (current_char == 's' ? LTDIV_CLAS : NO_STATE); break;
				case LTDIV_CLAS: state = (current_char == 's' ? LTDIV_CLASS : NO_STATE); break;
				case LTDIV_CLASS: state = (current_char == '=' ? LTDIV_CLASSEQ : current_char == ' ' ? LTDIV_CLASS : NO_STATE); break;
				case LTDIV_CLASSEQ: state = (current_char == '"' ? LTDIV_CLASSEQQT : current_char == ' ' ? LTDIV_CLASSEQ : NO_STATE); break;
					
				case LTSPAN_CLASSEQQT: state = (current_char == 'i' ? LTSPAN_CLASSEQQTI : NO_STATE); break;
				case LTSPAN_CLASSEQQTI: state = (current_char == 'd' ? LTSPAN_CLASSEQQTID : NO_STATE); break;
				case LTSPAN_CLASSEQQTID: state = (current_char == 'e' ? LTSPAN_CLASSEQQTIDE : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDE: state = (current_char == 'n' ? LTSPAN_CLASSEQQTIDEN : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDEN: state = (current_char == 't' ? LTSPAN_CLASSEQQTIDENT : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENT: state = (current_char == 'i' ? LTSPAN_CLASSEQQTIDENTI : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTI: state = (current_char == 'f' ? LTSPAN_CLASSEQQTIDENTIF : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIF: state = (current_char == 'i' ? LTSPAN_CLASSEQQTIDENTIFI : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIFI: state = (current_char == 'e' ? LTSPAN_CLASSEQQTIDENTIFIE : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIFIE: state = (current_char == 'r' ? LTSPAN_CLASSEQQTIDENTIFIER : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIFIER: state = (current_char == '"' ? LTSPAN_CLASSEQQTIDENTIFIERQT : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIFIERQT: state = (current_char == '>' ? LTSPAN_CLASSEQQTIDENTIFIERQTGT : current_char == ' ' ? LTSPAN_CLASSEQQTIDENTIFIERQT : NO_STATE); break;
				case LTSPAN_CLASSEQQTIDENTIFIERQTGT: state = (current_char == '<' ? NO_STATE : DIAGNOSIS_CODE); if (state == DIAGNOSIS_CODE) diagnosis_code += current_char;  break;
					
				case LTDIV_CLASSEQQT: state = (current_char == 'd' ? LTDIV_CLASSEQQTD : current_char == 'b' ? LTDIV_CLASSEQQTB : current_char == 'c' ? LTDIV_CLASSEQQTC : NO_STATE); break;
				case LTDIV_CLASSEQQTD: state = (current_char == 'e' ? LTDIV_CLASSEQQTDE : NO_STATE); break;
				case LTDIV_CLASSEQQTDE: state = (current_char == 's' ? LTDIV_CLASSEQQTDES : NO_STATE); break;
				case LTDIV_CLASSEQQTDES: state = (current_char == 'c' ? LTDIV_CLASSEQQTDESC : NO_STATE); break;
				case LTDIV_CLASSEQQTDESC: state = (current_char == 'r' ? LTDIV_CLASSEQQTDESCR : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCR: state = (current_char == 'i' ? LTDIV_CLASSEQQTDESCRI : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRI: state = (current_char == 'p' ? LTDIV_CLASSEQQTDESCRIP : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIP: state = (current_char == 't' ? LTDIV_CLASSEQQTDESCRIPT : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPT: state = (current_char == 'i' ? LTDIV_CLASSEQQTDESCRIPTI : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPTI: state = (current_char == 'o' ? LTDIV_CLASSEQQTDESCRIPTIO : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPTIO: state = (current_char == 'n' ? LTDIV_CLASSEQQTDESCRIPTION : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPTION: state = (current_char == '"' ? LTDIV_CLASSEQQTDESCRIPTIONQT : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPTIONQT: state = (current_char == '>' ? LTDIV_CLASSEQQTDESCRIPTIONQTGT : current_char == ' ' ? LTDIV_CLASSEQQTDESCRIPTIONQT : NO_STATE); break;
				case LTDIV_CLASSEQQTDESCRIPTIONQTGT: state = (current_char == '<' ? NO_STATE : DESCRIPTION); if (state == DESCRIPTION) description += current_char;  break;
					
				case LTDIV_CLASSEQQTB: state = (current_char == 'l' ? LTDIV_CLASSEQQTBL : NO_STATE); break;
				case LTDIV_CLASSEQQTBL: state = (current_char == 'u' ? LTDIV_CLASSEQQTBLU : NO_STATE); break;
				case LTDIV_CLASSEQQTBLU: state = (current_char == 'r' ? LTDIV_CLASSEQQTBLUR : NO_STATE); break;
				case LTDIV_CLASSEQQTBLUR: state = (current_char == 'b' ? LTDIV_CLASSEQQTBLURB : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURB: state = (current_char == 'S' ? LTDIV_CLASSEQQTBLURBS : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBS: state = (current_char == 'u' ? LTDIV_CLASSEQQTBLURBSU : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSU: state = (current_char == 'b' ? LTDIV_CLASSEQQTBLURBSUB : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUB: state = (current_char == 's' ? LTDIV_CLASSEQQTBLURBSUBS : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBS: state = (current_char == 'e' ? LTDIV_CLASSEQQTBLURBSUBSE : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSE: state = (current_char == 'c' ? LTDIV_CLASSEQQTBLURBSUBSEC : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSEC: state = (current_char == 't' ? LTDIV_CLASSEQQTBLURBSUBSECT : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECT: state = (current_char == 'i' ? LTDIV_CLASSEQQTBLURBSUBSECTI : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTI: state = (current_char == 'o' ? LTDIV_CLASSEQQTBLURBSUBSECTIO : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIO: state = (current_char == 'n' ? LTDIV_CLASSEQQTBLURBSUBSECTION : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTION: state = (current_char == 'H' ? LTDIV_CLASSEQQTBLURBSUBSECTIONH : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONH: state = (current_char == 'e' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHE : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHE: state = (current_char == 'a' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEA : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEA: state = (current_char == 'd' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEAD : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEAD: state = (current_char == 'i' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADI : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEADI: state = (current_char == 'n' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADIN : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEADIN: state = (current_char == 'g' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADING : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEADING: state = (current_char == '"' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQT : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQT: state = (current_char == '>' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQTGT : current_char == ' ' ? LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQT : NO_STATE); break;
				case LTDIV_CLASSEQQTBLURBSUBSECTIONHEADINGQTGT: state = BLURB; blurb += current_char;  break;
					
				// Definition(s)<img
				case D: state = (current_char == 'e' ? DE : NO_STATE); break;
				case DE: state = (current_char == 'f' ? DEF : NO_STATE); break;
				case DEF: state = (current_char == 'i' ? DEFI : NO_STATE); break;
				case DEFI: state = (current_char == 'n' ? DEFIN : NO_STATE); break;
				case DEFIN: state = (current_char == 'i' ? DEFINI : NO_STATE); break;
				case DEFINI: state = (current_char == 't' ? DEFINIT : NO_STATE); break;
				case DEFINIT: state = (current_char == 'i' ? DEFINITI : NO_STATE); break;
				case DEFINITI: state = (current_char == 'o' ? DEFINITIO : NO_STATE); break;
				case DEFINITIO: state = (current_char == 'n' ? DEFINITION : NO_STATE); break;
				case DEFINITION: state = (current_char == '(' ? DEFINITIONP : NO_STATE); break;
				case DEFINITIONP: state = (current_char == 's' ? DEFINITIONPS : NO_STATE); break;
				case DEFINITIONPS: state = (current_char == ')' ? DEFINITIONPSP : NO_STATE); break;
				case DEFINITIONPSP: state = (current_char == '<' ? DEFINITIONPSPLT : NO_STATE); break;
				case DEFINITIONPSPLT: state = (current_char == 'i' ? DEFINITIONPSPLTI : NO_STATE); break;
				case DEFINITIONPSPLTI: state = (current_char == 'm' ? DEFINITIONPSPLTIM : NO_STATE); break;
				case DEFINITIONPSPLTIM: state = (current_char == 'g' ? DEFINITIONPSPLTIMG : NO_STATE); break;
				case DEFINITIONPSPLTIMG: blurb_state = 1; state = NO_STATE; break;
				
				// Excludes <img
				case E: state = (current_char == 'x' ? EX : NO_STATE); break;
				case EX: state = (current_char == 'c' ? EXC : NO_STATE); break;
				case EXC: state = (current_char == 'l' ? EXCL : NO_STATE); break;
				case EXCL: state = (current_char == 'u' ? EXCLU : NO_STATE); break;
				case EXCLU: state = (current_char == 'd' ? EXCLUD : NO_STATE); break;
				case EXCLUD: state = (current_char == 'e' ? EXCLUDE : NO_STATE); break;
				case EXCLUDE: state = (current_char == 's' ? EXCLUDES : NO_STATE); break;
				case EXCLUDES: state = (current_char == ' ' ? EXCLUDES_ : NO_STATE); break;
				case EXCLUDES_: state = (current_char == '<' ? EXCLUDES_LT : NO_STATE); break;
				case EXCLUDES_LT: state = (current_char == 'i' ? EXCLUDES_LTI : NO_STATE); break;
				case EXCLUDES_LTI: state = (current_char == 'm' ? EXCLUDES_LTIM : NO_STATE); break;
				case EXCLUDES_LTIM: state = (current_char == 'g' ? EXCLUDES_LTIMG : NO_STATE); break;
				case EXCLUDES_LTIMG: blurb_state = 2; state = NO_STATE; break;
					
				// Applies To<img
				case A: state = (current_char == 'p' ? AP : NO_STATE); break;
				case AP: state = (current_char == 'p' ? APP : NO_STATE); break;
				case APP: state = (current_char == 'l' ? APPL : NO_STATE); break;
				case APPL: state = (current_char == 'i' ? APPLI : NO_STATE); break;
				case APPLI: state = (current_char == 'e' ? APPLIE : NO_STATE); break;
				case APPLIE: state = (current_char == 's' ? APPLIES : NO_STATE); break;
				case APPLIES: state = (current_char == ' ' ? APPLIES_ : NO_STATE); break;
				case APPLIES_: state = (current_char == 'T' ? APPLIES_T : NO_STATE); break;
				case APPLIES_T: state = (current_char == 'o' ? APPLIES_TO : NO_STATE); break;
				case APPLIES_TO: state = (current_char == '<' ? APPLIES_TOLT : NO_STATE); break;
				case APPLIES_TOLT: state = (current_char == 'i' ? APPLIES_TOLTI : NO_STATE); break;
				case APPLIES_TOLTI: state = (current_char == 'm' ? APPLIES_TOLTIM : NO_STATE); break;
				case APPLIES_TOLTIM: state = (current_char == 'g' ? APPLIES_TOLTIMG : NO_STATE); break;
				case APPLIES_TOLTIMG: blurb_state = 3; state = NO_STATE; break;
					
				// converts directly to:
				case C: state = (current_char == 'o' ? CO : NO_STATE); break;
				case CO: state = (current_char == 'n' ? CON : NO_STATE); break;
				case CON: state = (current_char == 'v' ? CONV : NO_STATE); break;
				case CONV: state = (current_char == 'e' ? CONVE : NO_STATE); break;
				case CONVE: state = (current_char == 'r' ? CONVER : NO_STATE); break;
				case CONVER: state = (current_char == 't' ? CONVERT : NO_STATE); break;
				case CONVERT: state = (current_char == 's' ? CONVERTS : NO_STATE); break;
				case CONVERTS: state = (current_char == ' ' ? CONVERTS_ : NO_STATE); break;
				case CONVERTS_: state = (current_char == 'd' ? CONVERTS_D : NO_STATE); break;
				case CONVERTS_D: state = (current_char == 'i' ? CONVERTS_DI : NO_STATE); break;
				case CONVERTS_DI: state = (current_char == 'r' ? CONVERTS_DIR : NO_STATE); break;
				case CONVERTS_DIR: state = (current_char == 'e' ? CONVERTS_DIRE : NO_STATE); break;
				case CONVERTS_DIRE: state = (current_char == 'c' ? CONVERTS_DIREC : NO_STATE); break;
				case CONVERTS_DIREC: state = (current_char == 't' ? CONVERTS_DIRECT : NO_STATE); break;
				case CONVERTS_DIRECT: state = (current_char == 'l' ? CONVERTS_DIRECTL : NO_STATE); break;
				case CONVERTS_DIRECTL: state = (current_char == 'y' ? CONVERTS_DIRECTLY : NO_STATE); break;
				case CONVERTS_DIRECTLY: state = (current_char == ' ' ? CONVERTS_DIRECTLY_ : NO_STATE); break;
				case CONVERTS_DIRECTLY_: state = (current_char == 't' ? CONVERTS_DIRECTLY_T : NO_STATE); break;
				case CONVERTS_DIRECTLY_T: state = (current_char == 'o' ? CONVERTS_DIRECTLY_TO : NO_STATE); break;
				case CONVERTS_DIRECTLY_TO: state = (current_char == ':' ? CONVERTS_DIRECTLY_TOC : NO_STATE); break;
				case CONVERTS_DIRECTLY_TOC: blurb_state = 4; state = BLURB; blurb += current_char; break;
					
				// <div class="contentBlurb">
				case LTDIV_CLASSEQQTC: state = (current_char == 'o' ? LTDIV_CLASSEQQTCO : NO_STATE); break;
				case LTDIV_CLASSEQQTCO: state = (current_char == 'n' ? LTDIV_CLASSEQQTCON : NO_STATE); break;
				case LTDIV_CLASSEQQTCON: state = (current_char == 't' ? LTDIV_CLASSEQQTCONT : NO_STATE); break;
				case LTDIV_CLASSEQQTCONT: state = (current_char == 'e' ? LTDIV_CLASSEQQTCONTE : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTE: state = (current_char == 'n' ? LTDIV_CLASSEQQTCONTEN : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTEN: state = (current_char == 't' ? LTDIV_CLASSEQQTCONTENT : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENT: state = (current_char == 'B' ? LTDIV_CLASSEQQTCONTENTB : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTB: state = (current_char == 'l' ? LTDIV_CLASSEQQTCONTENTBL : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBL: state = (current_char == 'u' ? LTDIV_CLASSEQQTCONTENTBLU : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBLU: state = (current_char == 'r' ? LTDIV_CLASSEQQTCONTENTBLUR : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBLUR: state = (current_char == 'b' ? LTDIV_CLASSEQQTCONTENTBLURB : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBLURB: state = (current_char == '"' ? LTDIV_CLASSEQQTCONTENTBLURBQT : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBLURBQT: state = (current_char == '>' ? LTDIV_CLASSEQQTCONTENTBLURBQTGT : NO_STATE); break;
				case LTDIV_CLASSEQQTCONTENTBLURBQTGT: blurb_state = 5; state = BLURB; blurb += current_char; break;
					
				//back-references
				case B: state = (current_char == 'a' ? BA : NO_STATE); break;
				case BA: state = (current_char == 'c' ? BAC : NO_STATE); break;
				case BAC: state = (current_char == 'k' ? BACK : NO_STATE); break;
				case BACK: state = (current_char == '-' ? BACKD : NO_STATE); break;
				case BACKD: state = (current_char == 'r' ? BACKDR : NO_STATE); break;
				case BACKDR: state = (current_char == 'e' ? BACKDRE : NO_STATE); break;
				case BACKDRE: state = (current_char == 'f' ? BACKDREF : NO_STATE); break;
				case BACKDREF: state = (current_char == 'e' ? BACKDREFE : NO_STATE); break;
				case BACKDREFE: state = (current_char == 'r' ? BACKDREFER : NO_STATE); break;
				case BACKDREFER: state = (current_char == 'e' ? BACKDREFERE : NO_STATE); break;
				case BACKDREFERE: state = (current_char == 'n' ? BACKDREFEREN : NO_STATE); break;
				case BACKDREFEREN: state = (current_char == 'c' ? BACKDREFERENC : NO_STATE); break;
				case BACKDREFERENC: state = (current_char == 'e' ? BACKDREFERENCE : NO_STATE); break;
				case BACKDREFERENCE: state = (current_char == 's' ? BACKDREFERENCES : NO_STATE); break;
				case BACKDREFERENCES: state = (current_char == '<' ? BACKDREFERENCESLT : BACKDREFERENCES); break;
				case BACKDREFERENCESLT: state = (current_char == 'd' ? BACKDREFERENCESLTD : BACKDREFERENCES); break;
				case BACKDREFERENCESLTD: state = (current_char == 'l' ? BACKDREFERENCESLTDL : BACKDREFERENCES); break;
				case BACKDREFERENCESLTDL: state = (current_char == '>' ? BACKDREFERENCESLTDLGT : BACKDREFERENCES); break;
					
				case BACKDREFERENCESLTDLGT: state = (current_char == '<' ? BACKDREFERENCESLTDLGTLT : BACKDREFERENCESLTDLGT); blurb += current_char; break;
				case BACKDREFERENCESLTDLGTLT: state = (current_char == '/' ? BACKDREFERENCESLTDLGTLTFS : BACKDREFERENCESLTDLGT); blurb += current_char; break;
				case BACKDREFERENCESLTDLGTLTFS: state = (current_char == 'd' ? BACKDREFERENCESLTDLGTLTFSD : BACKDREFERENCESLTDLGT); blurb += current_char; break;
				case BACKDREFERENCESLTDLGTLTFSD: state = (current_char == 'l' ? BACKDREFERENCESLTDLGTLTFSDL : BACKDREFERENCESLTDLGT); blurb += current_char; break;
				case BACKDREFERENCESLTDLGTLTFSDL: state = (current_char == '>' ? BACKDREFERENCESLTDLGTLTFSDLGT : BACKDREFERENCESLTDLGT); blurb += current_char; break;
				case BACKDREFERENCESLTDLGTLTFSDLGT: {
					back_references = "<dl>" + blurb;
					blurb = "";
					state = NO_STATE;
					break;
				}
					
				// <ul class="codeList">
				case LTU: state = (current_char == 'l' ? LTUL : NO_STATE); break;
				case LTUL: state = (current_char == ' ' ? LTUL_ : NO_STATE); break;
				case LTUL_: state = (current_char == 'c' ? LTUL_C : NO_STATE); break;
				case LTUL_C: state = (current_char == 'l' ? LTUL_CL : NO_STATE); break;
				case LTUL_CL: state = (current_char == 'a' ? LTUL_CLA : NO_STATE); break;
				case LTUL_CLA: state = (current_char == 's' ? LTUL_CLAS : NO_STATE); break;
				case LTUL_CLAS: state = (current_char == 's' ? LTUL_CLASS : NO_STATE); break;
				case LTUL_CLASS: state = (current_char == '=' ? LTUL_CLASSEQ : NO_STATE); break;
				case LTUL_CLASSEQ: state = (current_char == '"' ? LTUL_CLASSEQQT : NO_STATE); break;
				case LTUL_CLASSEQQT: state = (current_char == 'c' ? LTUL_CLASSEQQTC : NO_STATE); break;
				case LTUL_CLASSEQQTC: state = (current_char == 'o' ? LTUL_CLASSEQQTCO : NO_STATE); break;
				case LTUL_CLASSEQQTCO: state = (current_char == 'd' ? LTUL_CLASSEQQTCOD : NO_STATE); break;
				case LTUL_CLASSEQQTCOD: state = (current_char == 'e' ? LTUL_CLASSEQQTCODE : NO_STATE); break;
				case LTUL_CLASSEQQTCODE: state = (current_char == 'L' ? LTUL_CLASSEQQTCODEL : NO_STATE); break;
				case LTUL_CLASSEQQTCODEL: state = (current_char == 'i' ? LTUL_CLASSEQQTCODELI : NO_STATE); break;
				case LTUL_CLASSEQQTCODELI: state = (current_char == 's' ? LTUL_CLASSEQQTCODELIS : NO_STATE); break;
				case LTUL_CLASSEQQTCODELIS: state = (current_char == 't' ? LTUL_CLASSEQQTCODELIST : NO_STATE); break;
				case LTUL_CLASSEQQTCODELIST: state = (current_char == '"' ? LTUL_CLASSEQQTCODELISTQT : NO_STATE); break;
				case LTUL_CLASSEQQTCODELISTQT: state = (current_char == '>' ? LTUL_CLASSEQQTCODELISTQTGT : NO_STATE); break;
					
					
				case LTUL_CLASSEQQTCODELISTQTGT: state = (current_char == '<' ? LTUL_CLASSEQQTCODELISTQTGTLT : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLT: state = (current_char == 'l' ? LTUL_CLASSEQQTCODELISTQTGTLTL : current_char == '/' ? LTUL_CLASSEQQTCODELISTQTGTLTFS : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTL: state = (current_char == 'i' ? LTUL_CLASSEQQTCODELISTQTGTLTLI : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTLI: state = (current_char == '>' ? LTUL_CLASSEQQTCODELISTQTGTLTLIGT : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTLIGT: {
					
					blurb = blurb.substring(0, blurb.length() - 4);
					if (!blurb.isEmpty()) {
						System.out.println("adding blurb1 >" + blurb);
						index_entries.add(blurb);
						blurb = "";
					}
					blurb += current_char;
					state = LTUL_CLASSEQQTCODELISTQTGT;
					break;
				}
					
					/*
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFS = 210;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSU = 211;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSUL = 212;
	private static final short LTUL_CLASSEQQTCODELISTQTGTLTFSULGT = 213;
					 */
					
					
				case LTUL_CLASSEQQTCODELISTQTGTLTFS: state = (current_char == 'u' ? LTUL_CLASSEQQTCODELISTQTGTLTFSU : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTFSU: state = (current_char == 'l' ? LTUL_CLASSEQQTCODELISTQTGTLTFSUL : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTFSUL: state = (current_char == '>' ? LTUL_CLASSEQQTCODELISTQTGTLTFSULGT : LTUL_CLASSEQQTCODELISTQTGT); blurb += current_char; break;
				case LTUL_CLASSEQQTCODELISTQTGTLTFSULGT: {
					
					blurb = blurb.substring(0, blurb.length() - 5);
					System.out.println("adding blurb2 >" + blurb);
					index_entries.add(blurb);
					blurb = "";
					state = NO_STATE;
					break;
				}
					
			}
			
			//if (state > 132)
			//	System.out.println("state >" + state + "<");
			
			
		}
		
		System.out.println("found diagnosis_code >" + diagnosis_code + "<");
		System.out.println("found description >" + description + "<");
		
		System.out.println("found diagnosis_definitions >" + diagnosis_definitions + "<");
		System.out.println("found excludes >" + excludes + "<");
		System.out.println("found applies_to >" + applies_to + "<");
		System.out.println("found convert_to >" + convert_to + "<");
		System.out.println("found content >" + content + "<");
		System.out.println("found back_references >" + back_references + "<");
		
		
		
	}
	
	public DiagnosisCodeIndexEntry
	getIndexEntry(String _entry) {
		return null;
	}
	
}
