package edu.kaist.swrc.canceldetector;

import java.util.*;
import java.util.regex.Pattern;



public class CancelDetector{
	private RegularExpression re;
	private Pattern[] chgCluePtnList;
    private Pattern[] cnlCluePtnList; // woo. 2014-08-20 취소 단서 패턴 리스트 추가

    public CancelDetector(){
		super();
		// Set up regular expression dealing object.
		re							= new RegularExpression();
		LinkedList<Character> sharpLits	= new LinkedList<Character>();
		for(int i = 48; i <= 57; i++){
			sharpLits.add((char)i);
		}
		re.addNewNotation('#', sharpLits);
		
		// Generate patterns for chg-clues.
		chgCluePtnList	= new Pattern[changeClue.length];
		for(int i = 0; i < changeClue.length; i++){
			chgCluePtnList[i]	= re.changeRegex2JavaProcessablePattern(changeClue[i]); 
		}

        // woo. 2014-08-20 Generate patterns for cancel-clues. 추가
        cnlCluePtnList = new Pattern[cancelClue.length];
        for(int i = 0; i < cancelClue.length; i++) {
            cnlCluePtnList[i] = re.changeRegex2JavaProcessablePattern(cancelClue[i]);
        }
	}
	
	
	/**
	 * 자질의 이름과, 그 자질에 대한 값을 리턴한다.
	 * @return 이름-자질 맵핑 테이블.
	 */
	public boolean isUpdated(String txt){   // woo. 2014-08-20 isCanceled 에서 isUpdated 로 변경
		// Check rule ID: 52, 57, 107, 109, 116, 142.
		// Simplified form.		
		if( isTheTextContainChgClue(txt)){
			return true;
		}
		return false;
	}

    // woo. 2014-08-20 추가
    public boolean isCanceled(String txt) {
        if(isTheTextContainCnlClue(txt)) {
            return true;
        }
        return false;
    }
    private boolean isTheTextContainCnlClue(String text) {
        text	= text.replaceAll("\t", " ");
        text	= text.replaceAll("　", " ");

        for(int i = 0; i < cnlCluePtnList.length; i++) {
            int[] result = re.getMatchedText(text, cnlCluePtnList[i]);
            if(result.length > 0) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * Returns true, if the text contains one of the change-representing clues.
	 * @param text
	 * @return
	 */
	private boolean isTheTextContainChgClue(String text){
		text	= text.replaceAll("\t", " ");
		text	= text.replaceAll("　", " ");

		for(int i = 0; i < chgCluePtnList.length; i++){
			int[] result		= re.getMatchedText(text, chgCluePtnList[i]);
			if(result.length > 0){
				return true;
			}
		}
		return false;
	}


	

	/**
	 * 일정의 변경 또는 확정을 나타내는 단서.
	 * Development set을 통하여 얻어짐.
	 */
	private static String[] changeClue	= {"연기", "변경", "이동"};

    // woo. 2014-08-20 일정의 취소를 나타내는 단서 추가
    private static String[] cancelClue = {"취소"};
}
