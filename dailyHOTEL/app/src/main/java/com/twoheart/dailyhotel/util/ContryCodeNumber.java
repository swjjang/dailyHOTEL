package com.twoheart.dailyhotel.util;

import java.util.ArrayList;

/**
 * CountryCodeHeadNumber      v 1.0.1<br>
 * <p/>
 * <Br>
 * Android phone 의 국가 코드로 국가전화번호를 획득하는 Class 입니다.<br>
 * Android 에서 기본적으로 지역의 국가코드로 사용하는 ISO 3166-1 alpha-2 codes 에 근거하고 있습니다.<br>
 * 코드에 대한 국가명은 ISO 3166-1 alpha-2 codes 를 참고하시기 바랍니다.<br>
 * <br>
 * <br>
 * 0 으로 기록된 국제전화번호는 위성통신만 가능한 지역이거나 할당되지 않은 번호를 뜻합니다.(False number)<br>
 */
public class ContryCodeNumber
{
    private final String[][] CODE_NUMBER = new String[][]{{"", "", "", "주요국가"},//
        {"KR", "82", "KOREA, REPUBLIC OF", "대한민국"},//
        {"CN", "86", "CHINA", "중국"},//
        {"SG", "65", "SINGAPORE", "싱가폴"},//
        {"HK", "852", "HONG KONG", "홍콩"},//
        {"MO", "853", "MACAO", "마카오"},//
        {"TH", "66", "THAILAND", "태국"},//
        {"TW", "886", "TAIWAN, PROVINCE OF CHINA", "대만"},//
        {"AU", "61", "AUSTRALIA", "호주"},//
        {"NZ", "64", "NEW ZEALAND", "뉴질랜드"},//
        {"JP", "81", "JAPAN", "일본"},//
        {"US", "1", "UNITED STATES", "미국"},//

        {"", "", "", "ㄱ"},//
        {"GH", "233", "GHANA", "가나"},//
        {"GA", "241", "GABON", "가봉"},//
        {"GY", "594", "GUYANA", "가이아나"},//
        {"GG", "44", "GUERNSEY", "건지 (영국령)"},//
        {"GP", "590", "GUADELOUPE", "과들루프섬"},//
        {"GT", "502", "GUATEMALA", "과테말라"},//
        {"GU", "1671", "GUAM", "괌"},//
        {"GD", "1473", "GRENADA", "그레나다"},//
        {"GE", "995", "GEORGIA", "그루지아"},//
        {"GR", "30", "GREECE", "그리스"},//
        {"GL", "299", "GREENLAND", "그린란드"},//
        {"GN", "224", "GUINEA", "기니"},//
        {"GW", "245", "GUINEA-BISSAU", "기니비사우"},//

        {"", "", "", "ㄴ"},//
        {"NA", "264", "NAMIBIA", "나미비아"},//
        {"NR", "674", "NAURU", "나우루"},//
        {"NG", "234", "NIGERIA", "나이지리아"},//
        {"AQ", "672", "ANTARCTICA", "남극대륙"}, //
        {"TF", "672", "FRENCH SOUTHERN TERRITORIES", "남극지역 (프랑스령)"},//
        {"ZA", "27", "SOUTH AFRICA", "남아프리카"},//
        {"NL", "31", "NETHELANDS", "네덜란드"},//
        {"AN", "599", "NETHELANDS ANTILLES", "네덜란드령 안틸레스"},//
        {"NP", "977", "NEPAL", "네팔"},//
        {"NO", "47", "NORWAY", "노르웨이"},//
        {"NF", "672", "NORFOLK ISLAND", "노르폭섬"},//
        {"NC", "687", "NEW CALEDONIA", "뉴칼레도니아"},//
        {"NU", "683", "NIUE", "니우에"},//
        {"NE", "227", "NIGER", "니제르"},//
        {"NI", "505", "NICARAGUA", "니카라과"},//

        {"", "", "", "ㄷ"},//
        {"DK", "45", "DENMARK", "덴마크"},//
        {"DO", "1809", "DOMINICA REPUBLIC", "도미니카 공화국"},//
        {"DM", "1809", "DOMINICA", "도미니카"},//
        {"DE", "49", "GERMANY", "독일"},//
        {"TL", "670", "TIMOR-LESTE", "동티모르"},//

        {"", "", "", "ㄹ"},//
        {"LA", "856", "LAO PEOPLE'S DEMOCRATIC REPUBLIC", "라오스"},//
        {"LR", "231", "LIBERIA", "라이베리아"},//
        {"LV", "371", "LATVIA", "라트비아"},//
        {"RU", "7", "RUSSIAN FEDERATION", "러시아"},//
        {"LB", "961", "LEBANON", "레바논"},//
        {"LS", "266", "LESOTHO", "레소토"},//
        {"RE", "262", "REUNION", "레위니옹제도 (프랑즈령)"},//
        {"RQ", "40", "ROMANIA", "루마니아"},//
        {"LU", "352", "LUXEMBOURG", "룩셈부르크"},//
        {"RW", "250", "RWANDA", "르완다"},//
        {"LY", "218", "LIBYAN ARAB JAMAHIRIYA", "리비아사회주의인민아랍국"},//
        {"LT", "370", "LITHUANIA", "리투아니아"},//
        {"LI", "423", "LIECHTENSTEIN", "리히텐슈타인"},//

        {"", "", "", "ㅁ"},//
        {"MG", "261", "MADAGASCAR", "마다가스카르"},//
        {"MH", "692", "MARSHALL ISLANDS", "마샬제도"},//
        {"YT", "269", "MAYOTTE", "마요트"},//
        {"MK", "389", "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "마케도니아"},//
        {"MW", "265", "MALAWI", "말라위"},//
        {"MY", "60", "MALAYSIA", "말레이시아"},//
        {"ML", "223", "MALI", "말리"},//
        {"MQ", "59687", "MARTINQUE", "말티니크"},//
        {"IM", "44", "ISLE OF MAN", "맨섬"},//
        {"MX", "52", "MEXICO", "멕시코"},//
        {"MC", "377", "MONACO", "모나코"},//
        {"MA", "212", "MOROCCO", "모로코"},//
        {"MU", "230", "MAURITIUS", "모리셔스"},//
        {"MR", "222", "MAURITANIA", "모리타니아"},//
        {"MZ", "258", "MOZAMBIQUE", "모잠비크"},//
        {"ME", "382", "MONTENEGRO", "몬테네그로"}, //
        {"MS", "1664", "MONTSERRAT", "몬트세라트섬"},//
        {"MD", "373", "MOLDOVA", "몰도바"},//
        {"MV", "960", "MALDIVES", "몰디브"},//
        {"MT", "356", "MALTA", "몰타"},//
        {"MN", "976", "MONGOLIA", "몽골"},//
        {"MM", "95", "MYANMAR", "미얀마"},//
        {"FM", "691", "MICRONESIA, FEDERATED STATE OF", "미크로네시아 연방수도"},//

        {"", "", "", "ㅂ"},//
        {"VU", "678", "VANUATU", "바누아투"},//
        {"BH", "973", "BAHRAIN", "바레인"},//
        {"BB", "1246", "BARBADOS", "바베이도스"},//
        {"VA", "681", "HOLY SEE(VATICAN CITY STATE)", "바티간시국"},//
        {"BS", "1242", "BAHAMAS", "바하마제도"},//
        {"BD", "880", "BANGLADESH", "방글라데시"},//
        {"BT", "1441", "BERMUDA", "버뮤다"},//
        {"VI", "1340", "VIRGIN ISLANDS, U.S", "버진 아일랜드 (미국령)"},//
        {"VG", "1284", "VIRGIN ISLANDS, BRITISH", "버진 아일랜드 (영국령)"},//
        {"BM", "229", "BENIN", "베냉"},//
        {"VE", "58", "VENEZUELA", "베네수엘라"},//
        {"VN", "84", "VIET NAM", "베트남"},//
        {"BE", "32", "BELGIUM", "벨기에"},//
        {"BY", "375", "BELARUS", "벨라루스"},//
        {"BJ", "501", "BELIZE", "벨리즈"},//
        {"BA", "387", "BOSINIA AND HERZEGOVINA", "보즈니아 헤르체코비나"},//
        {"BW", "267", "BOTSWANA", "보츠와나"},//
        {"BO", "591", "BOLIVIA", "볼리비아"},//
        {"BI", "257", "BURUNDI", "부룬디"},//
        {"BF", "226", "BURKINA FASO", "부르키나파소"},//
        {"BV", "47", "BOUVET ISLAND", "부베 섬 (노르웨이령)"},//
        {"MP", "1670", "NORTHERN MARIANA ISLANDS", "북마리아나제도"},//
        {"KP", "85", "KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF", "북한"},//
        {"BG", "359", "BULGARIA", "불가리아"},//
        {"BR", "55", "BRAZIL", "브라질"},//
        {"BN", "673", "BRUNEI DARUSSALAM", "브루나이"},//
        {"VC", "1784", "SAINT VINCENT AND THE GRENADINES", "빈센트"},//

        {"", "", "", "ㅅ"},//
        {"SA", "966", "SAUDI ARABIA", "사우디 아라비아"},//
        {"GS", "44", "SOUTH GEORGIA AND THE SOURTH SANDWICH ISLANDS", "사우스조지아 사우스센드위치제도 (영국령)"},//
        {"CY", "357", "CYPRUS", "사이프러스"},//
        {"SM", "378", "SAN MARINO", "산마리노"},//
        {"ST", "239", "SAO TOME AND PRINCIPE", "상투메프린시페"},//
        {"PM", "508", "SAINT PIRRE AND MIQUELON", "생피에르미클롱"},//
        {"AS", "685", "AMERICAN SAMOA", "서사모아"},//
        {"WS", "685", "SAMOA", "서사모아"},//
        {"WF", "212", "WESTERN SAHARA", "서사하라 (모로코령)"},//
        {"SN", "221", "SENEGAL", "세네갈"},//
        {"RS", "381", "SERBIA", "세르비아"},//
        {"SC", "248", "SEYCHELLES", "세이셸"},//
        {"LC", "1758", "SAINT LUCIA", "세인트 루시아"},//
        {"MF", "590", "SAINT MARTIN", "세인트 마틴"},//
        {"BL", "97133", "SAINT BARTHELEMY", "세인트 바셀레미 (프랑스령)"},//
        {"KN", "1869", "SAINT KITTS AND NEVIS", "세인트키츠네비스"},//
        {"SH", "290", "SAINT HELENA", "세인트헬레나"},//
        {"SO", "252", "SOMALIA", "소말리아"},//
        {"SB", "677", "SOLOMON ISLANDS", "솔로몬 제도"},//
        {"SD", "249", "SUDAN", "수단"},//
        {"SR", "597", "SURINAME", "수리남"},//
        {"LK", "94", "SRI LANKA", "스리랑카"},//
        {"SJ", "47", "SVALBARD AND JAN MAYEN", "스발바르 얀 마옌"},//
        {"SZ", "268", "SWAZILAND", "스와질렌드"},//
        {"SE", "46", "SWEDEN", "스웨덴"},//
        {"CH", "41", "SWITZERLAND", "스위스"},//
        {"ES", "34", "SPAIN", "스페인"},//
        {"SK", "421", "SLOVAKIA", "슬로바키아"},//
        {"SI", "421", "SLOVENIA", "슬로베니아"},//
        {"SY", "963", "SIRIAN ARAB REPUBLIC", "시리아"},//
        {"SL", "232", "SIERRA LEONE", "씨에라리온"},//

        {"", "", "", "ㅇ"},//
        {"AE", "971", "UNITED ARAB EMIRATES", "아랍에미리트"},//
        {"AW", "297", "ARUBA", "아루바"},//
        {"AM", "374", "ARMENIA", "아르메니아"},//
        {"AR", "54", "ARGENTINA", "아르헨티나"},//
        {"IS", "354", "ICELAND", "아이슬랜드"},//
        {"HT", "509", "HAITI", "아이티"},//
        {"IE", "353", "IRELAND", "아일랜드"},//
        {"AZ", "994", "AZERBAIJAN", "아제르바이잔"},//
        {"AF", "93", "AFGHANISTAN", "아프가니스탄"},//
        {"AD", "376", "ANDORRA", "안도라"},//
        {"AL", "355", "ALBANIA", "알바니아"},//
        {"DZ", "213", "ALGERIA", "알제리아"},//
        {"AO", "244", "ANGOLA", "앙골라"},//
        {"AI", "1264", "ANGUILLA", "앵길라"},//
        {"ER", "291", "ERITREA", "에리트레아"},//
        {"EE", "372", "ESTONIA", "에스토니아"},//
        {"EC", "593", "ECUAADOR", "에콰도르"},//
        {"ET", "251", "ETHIOPIA", "에티오피아"},//
        {"AG", "1268", "ANTIGUA AND BARBUDA", "엔티가바부다"},//
        {"SV", "503", "EL SALVADOR", "엘살바도르"},//
        {"GB", "44", "UNITED KINGDOM", "영국"},//
        {"YE", "967", "YEMEN", "예멘"},//
        {"OM", "968", "OMAN", "오만"},//
        {"AT", "43", "AUSTRIA", "오스트리아"},//
        {"HN", "504", "HONDURAS", "온두라스"},//
        {"AX", "353", "ALAND ISLANDS", "올란드제도"},//
        {"JO", "962", "JORDAN", "요르단"},//
        {"UG", "256", "UGANDA", "우간다"},//
        {"UY", "598", "URUGUAY", "우루과이"},//
        {"UZ", "998", "UZBEKISTAN", "우즈베키스탄"},//
        {"UA", "380", "UKRAINE", "우크라이나"},//
        {"IQ", "964", "IRAQ", "이라크"},//
        {"IR", "98", "IRAN, ISLAMIC REPUBLIC OF", "이란"},//
        {"IL", "972", "ISRAEL", "이스라엘"},//
        {"EG", "20", "EGYPT", "이집트"},//
        {"IT", "39", "ITERY", "이탈리아"},//
        {"IN", "91", "INDIA", "인도"},//
        {"ID", "62", "INDONESIA", "인도네시아"},//
        {"IO", "246", "BRITISH INDIAN OCEAN TERRIRORY", "인디언특별보호구"},//

        {"", "", "", "ㅈ"},//
        {"JM", "1876", "JAMAICA", "자메이카"},//
        {"ZW", "263", "ZIMBABWE", "잠바브웨"},//
        {"GM", "260", "GAMBIA", "잠비아"},//
        {"ZM", "260", "ZAMBIA", "잠비아"},//
        {"JE", "44", "JERSEY", "저지"},//
        {"GQ", "240", "EQUATORIAL GUINEA", "적도 기니"},//
        {"CF", "236", "CENTRAL AFRICAN REPUBLIC", "중앙아프리카공화국"},//
        {"DJ", "253", "DJIBOUTI", "지부티"},//
        {"GI", "350", "GIBRALTAR", "지브롤터"},//

        {"", "", "", "ㅊ"},//
        {"TD", "235", "CHAD", "차드"},//
        {"CZ", "420", "CZECH REPUBLIC", "체코"},//
        {"CL", "56", "CHILE", "칠레"},//

        {"", "", "", "ㅋ"},//
        {"CM", "237", "CAMEROON", "카메룬"},//
        {"CV", "238", "CAPE VERDE", "카보베르데"},//
        {"KZ", "73", "KAZAKHSTAN", "카자흐스탄"},//
        {"QA", "974", "QATAR", "카타르"},//
        {"KH", "855", "CAMBODIA", "캄보디아"},//
        {"CA", "1", "CANADA", "캐나다"},//
        {"KE", "255", "KENYA", "케냐"},//
        {"KY", "1345", "CAYMAN ISLAND", "케이만제도"},//
        {"CI", "225", "COTE D'IVOIRE", "코르디부아르"},//
        {"KM", "269", "COMOROS", "코모로"},//
        {"CR", "506", "COSTA RICA", "코스타리카"},//
        {"CC", "61691", "COCOS(KELLING) ISLANDS", "코코스제도"},//
        {"CO", "57", "COLOMBIA", "콜롬비아"},//
        {"CG", "242", "CONGO", "콩고"},//
        {"CD", "243", "CONGO, THE DEMOCRATIC REPUBLIC OF THE", "콩고민주공화국"},//
        {"CU", "53", "CUBA", "쿠바"},//
        {"KW", "965", "KUWAIT", "쿠웨이트"},//
        {"CK", "682", "COOK ISLANDS", "쿡크제도"},//
        {"HR", "385", "CROATIA", "크로아티아"},//
        {"CX", "6191", "CHRISMAS ISLAND", "크리스마스섬"},//
        {"KI", "686", "KIRIBATI", "키리바티"},//
        {"KG", "992", "KYRGYZSTAN", "키리지스탄"},//

        {"", "", "", "ㅌ"},//
        {"TJ", "992", "TAJKISTAN", "타지키스탄"},//
        {"TZ", "255", "TANZANIA, UNITED REPUBLIC OF", "탄자니아 공화국"},//
        {"TR", "90", "TURKEY", "터키"},//
        {"TC", "1649", "TURKS AND CAICOS ISLANDS", "턱스케이코스제도"},//
        {"TG", "228", "TOGO", "토고"},//
        {"TK", "690", "TOKELAU", "토켈라우"},//
        {"TO", "676", "TONGA", "통가"},//
        {"TM", "993", "TURKMENISTAN", "투르크메니스탄"},//
        {"TV", "688", "TUVALU", "투발루"},//
        {"TN", "216", "TUNISIA", "튀니지"},//
        {"TT", "1868", "TRINIDAD AND TOBAGO", "트리니다드 토바고"},//

        {"", "", "", "ㅍ"},//
        {"PA", "507", "PANAMA", "파나마"},//
        {"PY", "595", "PARAGUAY", "파라과이"},//
        {"PK", "92", "PAKISTAN", "파키스탄"},//
        {"PG", "675", "PAPUA NEW GUINEA", "파푸아 뉴기니"},//
        {"PW", "680", "PALAU", "팔라우"},//
        {"PS", "970", "PALESTINIAN TERRIROTY, OCCUPIED", "팔레스타인"},//
        {"FO", "298", "FAROE ISLANDS", "페로제도"},//
        {"PE", "51", "PERU", "페루"},//
        {"PT", "351", "PORTUGAL", "포르투갈"},//
        {"FK", "500", "FALKLAND ISLANDS (MALVINAS)", "포클랜드제도"},//
        {"PL", "48", "POLAND", "폴란드"},//
        {"PF", "685", "FRENCHPOYNESIA", "폴리네시아 (프랑스령)"},//
        {"PR", "1787", "PUERTO RICO", "푸에르토리코"},//
        {"FR", "33", "FRANCE", "프랑스"},//
        {"GF", "594", "FRENCH GUIANA", "프랑스령 기아나"},//
        {"FJ", "679", "FIJI", "피지"},//
        {"FI", "358", "FINLAND", "핀란드"},//
        {"PH", "63", "PHLIPPINES", "필리핀"},//

        {"", "", "", "ㅎ"},//
        {"HU", "36", "HUNGARY", "헝가리"},//
    };

    public String getContryPhoneNumber(int index)
    {
        if (index >= CODE_NUMBER.length)
        {
            return null;
        }

        return CODE_NUMBER[index][1];
    }

    public String getKoreanContryName(int index)
    {
        if (index >= CODE_NUMBER.length)
        {
            return null;
        }

        return CODE_NUMBER[index][3];
    }

    public ArrayList<String[]> getCountryValue()
    {
        int length = CODE_NUMBER.length;

        ArrayList<String[]> values = new ArrayList<String[]>(length);

        for (int i = 0; i < length; i++)
        {
            if (Util.isTextEmpty(CODE_NUMBER[i][1]) == true)
            {
                values.add(new String[]{CODE_NUMBER[i][3], null});
            } else
            {
                values.add(new String[]{CODE_NUMBER[i][3], "+" + CODE_NUMBER[i][1]});
            }
        }

        return values;
    }


    /**
     * ISO코드를 이용하여 해당국가의 국제번호를 획득합니다. 존재하지 않는경우 Null 을 반환합니다.
     */
    public String getContryPhoneNumber(String isoString)
    {
        String result = "";

        for (int i = 0; i < CODE_NUMBER.length; i++)
        {
            if (isoString.equals(CODE_NUMBER[i][0]))
            {
                result = this.CODE_NUMBER[i][1];
                break;
            }
        }

        return result;
    }


    /**
     * ISO코드를 이용하여 해당국가명을 한국어로 획득합니다.
     */
    public String getKoreanContryName(String isoString)
    {
        String result = "";

        for (int i = 0; i < CODE_NUMBER.length; i++)
        {
            if (CODE_NUMBER[i][0].equals(isoString) && CODE_NUMBER[i].length > 3)
            {
                result = CODE_NUMBER[i][3];
                break;
            }
        }

        return result;
    }


    /**
     * ISO코드를 이용하여 해당국가명을 영어로 획득합니다.
     */
    public String getEnglishContryName(String isoString)
    {
        String result = "";

        for (int i = 0; i < CODE_NUMBER.length; i++)
        {
            if (CODE_NUMBER[i][0].equals(isoString))
            {
                result = CODE_NUMBER[i][2];
                break;
            }
        }

        return result;
    }

    /**
     * 전화번호 앞에 +기호를 확인하여 국제전화번호를 완성시켜주는 Method 입니다.<br>
     * 전화번호앞에 +기호의 유무를 확인하여 국제전화코드가 삽입되어있는지 검사후 없으면 해당 국가번호를<br>
     * + 코드와 삽입하여 반환합니다. <br>
     * <br>
     * isoCode는 ISO 3166-1 alpha2 code 두자리가 필요합니다.
     */
    public String insertCountryNumber(String phoneNumber, String isoCode)
    {
        String result = "";

        if (!phoneNumber.substring(0, 1).equals("+"))
        {
            result = "+" + getContryPhoneNumber(isoCode) + (phoneNumber.substring(1));
        } else
        {
            result = phoneNumber;
        }

        return result;
    }

    public boolean hasCountryCode(String countryCode)
    {
        if (Util.isTextEmpty(countryCode) == true)
        {
            return false;
        }

        countryCode = countryCode.replace("+", "").trim();

        for (int i = 0; i < CODE_NUMBER.length; i++)
        {
            if (CODE_NUMBER[i][1].equals(countryCode))
            {
                return true;
            }
        }

        return false;
    }

    public String getCountry(String countryCode)
    {
        if (Util.isTextEmpty(countryCode) == true)
        {
            return null;
        }

        countryCode = countryCode.replace("+", "").trim();

        for (int i = 0; i < CODE_NUMBER.length; i++)
        {
            if (CODE_NUMBER[i][1].equals(countryCode))
            {
                return CODE_NUMBER[i][3];
            }
        }

        return null;
    }
}