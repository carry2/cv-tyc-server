package cn.com.chinaventure.cv.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Other.DoubleArrayTrieSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;

import cn.com.chinaventure.cv.common.annotation.RouteBind;
import cn.com.chinaventure.cv.entity.dto.HanlpDTO;

/**
 * Created by YZP on 2018/4/17.
 */
@RouteBind("v1/hanlp")
public class HanlpController extends Controller {
    private Logger log = LoggerFactory.getLogger(HanlpController.class);

    public void index() {

        log.debug("v1/index");
        renderText("测试");
    }

    public void ent() {
        String readData = HttpKit.readData(getRequest());
        HanlpDTO dto = FastJson.getJson().parse(readData, HanlpDTO.class);
        log.info(dto.toString());
        String text = dto.getText();
        //设置返回结果词性不显示
        HanLP.Config.ShowTermNature = false;
        DoubleArrayTrieSegment doubleArrayTrieSegment = new DoubleArrayTrieSegment();
        //关闭其他词典 只使用自定义
        doubleArrayTrieSegment.enableCustomDictionary(false).enableAllNamedEntityRecognize(false).enableCustomDictionaryForcing(false).enableNameRecognize(false).enableOrganizationRecognize(false).enablePartOfSpeechTagging(true).enablePlaceRecognize(false).enableTranslatedNameRecognize(false);
        List<String> result=new ArrayList<String>();
//        String[] fullCoreDictionaryPathArray = HanLP.Config.getFullCoreDictionaryPathArray();
//        for (String fullCoreDicPath:fullCoreDictionaryPathArray) {
//            long start3 = System.currentTimeMillis();
//            HanLP.Config.CoreDictionaryPath = fullCoreDicPath;
//            CoreDictionary.reload( HanLP.Config.CustomDictionaryPath[0]);
//           doubleArrayTrieSegment.trie = CoreDictionary.trie;
            List<Term> seg2 = doubleArrayTrieSegment.seg(text);
            if (seg2 != null) {
                for (Term term : seg2) {
                    result.add(term.toString());
                }
            }
            log.info("分词结果：{}", result.toArray().toString());
//            System.out.println("查询耗时" + (System.currentTimeMillis() - start3) + "ms");
//       }
        renderJson("resultList", result);
    }
}