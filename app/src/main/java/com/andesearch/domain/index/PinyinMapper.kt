package com.andesearch.domain.index

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinyinMapper @Inject constructor() {

    fun toPinyin(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val py = toSinglePinyin(ch)
            if (py != null) {
                if (sb.isNotEmpty() && sb.last() != ' ') sb.append(' ')
                sb.append(py)
            }
        }
        return sb.toString().lowercase()
    }

    fun toInitials(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val py = toSinglePinyin(ch)
            if (py != null) {
                sb.append(py[0])
            }
        }
        return sb.toString().lowercase()
    }

    companion object {
        private val PINYIN_MAP: Map<Char, String> = buildMap {
            // a - ai
            put('啊', "a"); put('阿', "a")
            put('爱', "ai"); put('矮', "ai"); put('挨', "ai"); put('哀', "ai")
            put('安', "an"); put('按', "an"); put('暗', "an"); put('案', "an"); put('岸', "an"); put('俺', "an")
            put('昂', "ang")
            put('奥', "ao"); put('傲', "ao"); put('凹', "ao")

            // ba - bai - ban - bang - bao
            put('八', "ba"); put('把', "ba"); put('吧', "ba"); put('爸', "ba"); put('巴', "ba"); put('拔', "ba"); put('霸', "ba")
            put('白', "bai"); put('百', "bai"); put('败', "bai"); put('拜', "bai"); put('摆', "bai")
            put('版', "ban"); put('办', "ban"); put('半', "ban"); put('班', "ban"); put('般', "ban"); put('搬', "ban"); put('板', "ban"); put('伴', "ban"); put('版', "ban")
            put('帮', "bang"); put('棒', "bang"); put('绑', "bang"); put('傍', "bang")
            put('包', "bao"); put('报', "bao"); put('保', "bao"); put('宝', "bao"); put('暴', "bao"); put('爆', "bao"); put('薄', "bao")

            // bei - ben - beng - bi - bian - biao - bie - bin - bing
            put('被', "bei"); put('北', "bei"); put('备', "bei"); put('背', "bei"); put('杯', "bei"); put('碑', "bei"); put('悲', "bei"); put('辈', "bei"); put('贝', "bei")
            put('本', "ben"); put('笨', "ben"); put('奔', "ben")
            put('蹦', "beng"); put('崩', "beng")
            put('比', "bi"); put('笔', "bi"); put('必', "bi"); put('毕', "bi"); put('闭', "bi"); put('逼', "bi"); put('壁', "bi"); put('避', "bi"); put('币', "bi"); put('臂', "bi")
            put('边', "bian"); put('变', "bian"); put('便', "bian"); put('编', "bian"); put('遍', "bian"); put('辩', "bian"); put('鞭', "bian")
            put('表', "biao"); put('标', "biao"); put('彪', "biao")
            put('别', "bie")
            put('宾', "bin")
            put('并', "bing"); put('病', "bing"); put('冰', "bing"); put('兵', "bing"); put('饼', "bing")

            // bo - bu
            put('不', "bu"); put('部', "bu"); put('步', "bu"); put('补', "bu"); put('捕', "bu"); put('布', "bu")
            put('波', "bo"); put('播', "bo"); put('博', "bo"); put('伯', "bo"); put('薄', "bo")

            // ca - cai - can - cang - cao - ce - cen - ceng
            put('擦', "ca")
            put('才', "cai"); put('财', "cai"); put('采', "cai"); put('彩', "cai"); put('菜', "cai"); put('裁', "cai"); put('猜', "cai"); put('材', "cai")
            put('参', "can"); put('残', "can"); put('餐', "can"); put('惨', "can")
            put('藏', "cang"); put('仓', "cang"); put('苍', "cang")
            put('草', "cao"); put('操', "cao"); put('槽', "cao"); put('曹', "cao")
            put('测', "ce"); put('侧', "ce"); put('策', "ce"); put('册', "ce")
            put('层', "ceng"); put('曾', "ceng")

            // cha - chai - chan - chang - chao - che - chen - cheng
            put('查', "cha"); put('差', "cha"); put('茶', "cha"); put('察', "cha"); put('插', "cha"); put('叉', "cha")
            put('拆', "chai"); put('柴', "chai")
            put('产', "chan"); put('颤', "chan"); put('缠', "chan")
            put('长', "chang"); put('场', "chang"); put('常', "chang"); put('厂', "chang"); put('唱', "chang"); put('尝', "chang"); put('偿', "chang")
            put('超', "chao"); put('朝', "chao"); put('潮', "chao"); put('炒', "chao"); put('抄', "chao")
            put('车', "che"); put('彻', "che"); put('撤', "che")
            put('陈', "chen"); put('称', "chen"); put('沉', "chen"); put('晨', "chen"); put('趁', "chen"); put('臣', "chen")
            put('成', "cheng"); put('程', "cheng"); put('城', "cheng"); put('称', "cheng"); put('承', "cheng"); put('诚', "cheng"); put('惩', "cheng"); put('呈', "cheng")

            // chi - chong - chou - chu - chuai - chuan - chuang - chui - chun - chuo
            put('吃', "chi"); put('持', "chi"); put('尺', "chi"); put('赤', "chi"); put('驰', "chi"); put('迟', "chi"); put('齿', "chi"); put('斥', "chi")
            put('重', "chong"); put('冲', "chong"); put('虫', "chong"); put('崇', "chong")
            put('抽', "chou"); put('愁', "chou"); put('仇', "chou"); put('丑', "chou"); put('臭', "chou"); put('筹', "chou")
            put('出', "chu"); put('处', "chu"); put('初', "chu"); put('除', "chu"); put('楚', "chu"); put('础', "chu"); put('储', "chu"); put('触', "chu"); put('畜', "chu")
            put('穿', "chuan"); put('传', "chuan"); put('船', "chuan"); put('串', "chuan"); put('喘', "chuan")
            put('创', "chuang"); put('窗', "chuang"); put('床', "chuang")
            put('吹', "chui"); put('垂', "chui"); put('锤', "chui")
            put('春', "chun"); put('纯', "chun"); put('唇', "chun"); put('醇', "chun")
            put('戳', "chuo")

            // ci - cong - cou - cu - cuan - cui - cun - cuo
            put('次', "ci"); put('此', "ci"); put('词', "ci"); put('辞', "ci"); put('磁', "ci"); put('刺', "ci")
            put('从', "cong"); put('丛', "cong"); put('聪', "cong")
            put('凑', "cou")
            put('促', "cu"); put('粗', "cu"); put('醋', "cu")
            put('催', "cui"); put('脆', "cui"); put('翠', "cui"); put('粹', "cui")
            put('存', "cun"); put('村', "cun"); put('寸', "cun")
            put('错', "cuo"); put('措', "cuo"); put('挫', "cuo")

            // da - dai - dan - dang - dao - de - deng - di - dian - diao - die - ding - diu
            put('大', "da"); put('打', "da"); put('达', "da"); put('答', "da"); put('搭', "da")
            put('带', "dai"); put('代', "dai"); put('待', "dai"); put('袋', "dai"); put('戴', "dai"); put('呆', "dai"); put('贷', "dai")
            put('但', "dan"); put('单', "dan"); put('蛋', "dan"); put('弹', "dan"); put('淡', "dan"); put('担', "dan"); put('胆', "dan"); put('诞', "dan")
            put('当', "dang"); put('档', "dang"); put('党', "dang"); put('挡', "dang"); put('荡', "dang")
            put('到', "dao"); put('道', "dao"); put('导', "dao"); put('倒', "dao"); put('刀', "dao"); put('岛', "dao"); put('盗', "dao"); put('稻', "dao")
            put('的', "de"); put('得', "de"); put('德', "de")
            put('等', "deng"); put('灯', "deng"); put('登', "deng"); put('邓', "deng"); put('瞪', "deng")
            put('地', "di"); put('第', "di"); put('低', "di"); put('底', "di"); put('敌', "di"); put('弟', "di"); put('滴', "di"); put('递', "di"); put('帝', "di")
            put('点', "dian"); put('电', "dian"); put('店', "dian"); put('典', "dian"); put('垫', "dian"); put('殿', "dian")
            put('调', "diao"); put('掉', "diao"); put('吊', "diao"); put('雕', "diao"); put('钓', "diao")
            put('跌', "die"); put('叠', "die"); put('蝶', "die"); put('爹', "die")
            put('定', "ding"); put('顶', "ding"); put('订', "ding"); put('盯', "ding"); put('钉', "ding"); put('鼎', "ding")
            put('丢', "diu")

            // dong - dou - du - duan - dui - dun - duo
            put('动', "dong"); put('东', "dong"); put('冬', "dong"); put('懂', "dong"); put('洞', "dong"); put('冻', "dong"); put('栋', "dong"); put('董', "dong")
            put('都', "dou"); put('斗', "dou"); put('豆', "dou"); put('抖', "dou"); put('逗', "dou")
            put('读', "du"); put('度', "du"); put('独', "du"); put('堵', "du"); put('赌', "du"); put('毒', "du"); put('渡', "du"); put('杜', "du"); put('督', "du")
            put('段', "duan"); put('短', "duan"); put('断', "duan"); put('端', "duan"); put('锻', "duan")
            put('对', "dui"); put('队', "dui"); put('堆', "dui"); put('兑', "dui")
            put('吨', "dun"); put('顿', "dun"); put('蹲', "dun"); put('盾', "dun"); put('钝', "dun")
            put('多', "duo"); put('夺', "duo"); put('朵', "duo"); put('躲', "duo"); put('堕', "duo")

            // e - en - er
            put('二', "er"); put('而', "er"); put('儿', "er"); put('耳', "er"); put('尔', "er")
            put('恩', "en")
            put('恶', "e"); put('额', "e"); put('俄', "e"); put('鹅', "e"); put('饿', "e"); put('扼', "e")

            // fa - fan - fang - fei - fen - feng - fo - fou - fu
            put('发', "fa"); put('法', "fa"); put('罚', "fa"); put('乏', "fa"); put('伐', "fa")
            put('反', "fan"); put('饭', "fan"); put('翻', "fan"); put('犯', "fan"); put('凡', "fan"); put('繁', "fan"); put('范', "fan"); put('烦', "fan"); put('返', "fan")
            put('方', "fang"); put('放', "fang"); put('房', "fang"); put('防', "fang"); put('访', "fang"); put('仿', "fang"); put('纺', "fang"); put('芳', "fang")
            put('非', "fei"); put('飞', "fei"); put('费', "fei"); put('肥', "fei"); put('废', "fei"); put('匪', "fei"); put('肺', "fei")
            put('分', "fen"); put('份', "fen"); put('粉', "fen"); put('纷', "fen"); put('奋', "fen"); put('愤', "fen"); put('坟', "fen"); put('芬', "fen")
            put('风', "feng"); put('封', "feng"); put('疯', "feng"); put('峰', "feng"); put('丰', "feng"); put('奉', "feng"); put('凤', "feng"); put('逢', "feng"); put('缝', "feng")
            put('佛', "fo")
            put('否', "fou")
            put('服', "fu"); put('父', "fu"); put('夫', "fu"); put('附', "fu"); put('负', "fu"); put('富', "fu"); put('复', "fu"); put('府', "fu"); put('副', "fu"); put('福', "fu"); put('妇', "fu"); put('付', "fu"); put('扶', "fu"); put('浮', "fu"); put('幅', "fu"); put('符', "fu"); put('腐', "fu")

            // ga - gai - gan - gang - gao - ge - gei - gen - geng - gong - gou - gu
            put('尬', "ga")
            put('改', "gai"); put('该', "gai"); put('盖', "gai"); put('概', "gai"); put('钙', "gai")
            put('感', "gan"); put('干', "gan"); put('敢', "gan"); put('赶', "gan"); put('肝', "gan"); put('杆', "gan"); put('甘', "gan")
            put('刚', "gang"); put('钢', "gang"); put('港', "gang"); put('岗', "gang"); put('杠', "gang"); put('纲', "gang")
            put('高', "gao"); put('告', "gao"); put('搞', "gao"); put('稿', "gao"); put('糕', "gao")
            put('个', "ge"); put('各', "ge"); put('哥', "ge"); put('歌', "ge"); put('格', "ge"); put('革', "ge"); put('隔', "ge"); put('葛', "ge"); put('割', "ge"); put('阁', "ge")
            put('给', "gei")
            put('跟', "gen"); put('根', "gen")
            put('更', "geng"); put('耕', "geng")
            put('工', "gong"); put('公', "gong"); put('共', "gong"); put('供', "gong"); put('功', "gong"); put('攻', "gong"); put('宫', "gong"); put('恭', "gong"); put('巩', "gong"); put('贡', "gong")
            put('狗', "gou"); put('够', "gou"); put('构', "gou"); put('购', "gou"); put('沟', "gou"); put('勾', "gou")
            put('古', "gu"); put('故', "gu"); put('股', "gu"); put('鼓', "gu"); put('顾', "gu"); put('谷', "gu"); put('骨', "gu"); put('固', "gu"); put('雇', "gu"); put('孤', "gu"); put('姑', "gu")

            // gua - guai - guan - guang - gui - gun - guo
            put('挂', "gua"); put('瓜', "gua"); put('刮', "gua"); put('寡', "gua")
            put('怪', "guai"); put('拐', "guai"); put('乖', "guai")
            put('关', "guan"); put('管', "guan"); put('观', "guan"); put('官', "guan"); put('馆', "guan"); put('冠', "guan"); put('惯', "guan"); put('灌', "guan"); put('贯', "guan")
            put('光', "guang"); put('广', "guang"); put('逛', "guang"); put('犷', "guang")
            put('贵', "gui"); put('鬼', "gui"); put('归', "gui"); put('规', "gui"); put('桂', "gui"); put('跪', "gui"); put('柜', "gui"); put('龟', "gui"); put('轨', "gui")
            put('滚', "gun"); put('棍', "gun")
            put('过', "guo"); put('国', "guo"); put('果', "guo"); put('锅', "guo"); put('郭', "guo")

            // ha - hai - han - hang - hao - he - hei - hen - heng - hong - hou - hu
            put('哈', "ha")
            put('还', "hai"); put('海', "hai"); put('害', "hai"); put('孩', "hai")
            put('汉', "han"); put('喊', "han"); put('含', "han"); put('寒', "han"); put('汗', "han"); put('韩', "han"); put('旱', "han")
            put('行', "hang"); put('航', "hang"); put('杭', "hang")
            put('好', "hao"); put('号', "hao"); put('毫', "hao"); put('豪', "hao"); put('耗', "hao"); put('浩', "hao")
            put('和', "he"); put('合', "he"); put('何', "he"); put('河', "he"); put('喝', "he"); put('核', "he"); put('盒', "he"); put('贺', "he"); put('荷', "he"); put('赫', "he"); put('黑', "hei")
            put('很', "hen"); put('恨', "hen"); put('狠', "hen"); put('痕', "hen")
            put('横', "heng"); put('恒', "heng"); put('衡', "heng")
            put('红', "hong"); put('宏', "hong"); put('洪', "hong"); put('轰', "hong"); put('虹', "hong"); put('鸿', "hong")
            put('后', "hou"); put('候', "hou"); put('厚', "hou"); put('侯', "hou"); put('喉', "hou"); put('猴', "hou")
            put('胡', "hu"); put('湖', "hu"); put('虎', "hu"); put('呼', "hu"); put('户', "hu"); put('互', "hu"); put('护', "hu"); put('忽', "hu"); put('壶', "hu"); put('糊', "hu")

            // hua - huai - huan - huang - hui - hun - huo
            put('花', "hua"); put('话', "hua"); put('化', "hua"); put('画', "hua"); put('华', "hua"); put('划', "hua"); put('滑', "hua")
            put('坏', "huai"); put('怀', "huai"); put('淮', "huai")
            put('换', "huan"); put('欢', "huan"); put('环', "huan"); put('缓', "huan"); put('患', "huan"); put('幻', "huan"); put('唤', "huan")
            put('黄', "huang"); put('慌', "huang"); put('皇', "huang"); put('煌', "huang"); put('晃', "huang")
            put('会', "hui"); put('回', "hui"); put('灰', "hui"); put('挥', "hui"); put('辉', "hui"); put('毁', "hui"); put('悔', "hui"); put('惠', "hui"); put('汇', "hui"); put('慧', "hui")
            put('婚', "hun"); put('混', "hun"); put('魂', "hun"); put('浑', "hun")
            put('火', "huo"); put('活', "huo"); put('或', "huo"); put('伙', "huo"); put('获', "huo"); put('货', "huo"); put('霍', "huo")

            // ji - jia - jian - jiang - jiao - jie - jin - jing - jiong - jiu - ju
            put('几', "ji"); put('机', "ji"); put('记', "ji"); put('及', "ji"); put('计', "ji"); put('级', "ji"); put('极', "ji");
            put('集', "ji"); put('基', "ji"); put('即', "ji"); put('技', "ji"); put('既', "ji"); put('急', "ji"); put('际', "ji");
            put('纪', "ji"); put('击', "ji"); put('积', "ji"); put('继', "ji"); put('迹', "ji"); put('剂', "ji"); put('寄', "ji");
            put('季', "ji"); put('济', "ji"); put('激', "ji"); put('吉', "ji"); put('鸡', "ji"); put('绩', "ji"); put('辑', "ji")
            put('家', "jia"); put('加', "jia"); put('价', "jia"); put('假', "jia"); put('架', "jia"); put('甲', "jia"); put('驾', "jia"); put('嫁', "jia"); put('夹', "jia"); put('佳', "jia")
            put('见', "jian"); put('件', "jian"); put('间', "jian"); put('建', "jian"); put('检', "jian"); put('简', "jian"); put('减', "jian"); put('渐', "jian"); put('健', "jian");
            put('坚', "jian"); put('践', "jian"); put('剑', "jian"); put('鉴', "jian"); put('键', "jian"); put('箭', "jian"); put('荐', "jian"); put('舰', "jian")
            put('将', "jiang"); put('讲', "jiang"); put('降', "jiang"); put('奖', "jiang"); put('江', "jiang"); put('疆', "jiang"); put('蒋', "jiang"); put('浆', "jiang")
            put('叫', "jiao"); put('教', "jiao"); put('交', "jiao"); put('脚', "jiao"); put('角', "jiao"); put('较', "jiao"); put('焦', "jiao");
            put('骄', "jiao"); put('郊', "jiao"); put('胶', "jiao"); put('搅', "jiao"); put('缴', "jiao"); put('轿', "jiao")
            put('接', "jie"); put('结', "jie"); put('节', "jie"); put('解', "jie"); put('界', "jie"); put('姐', "jie"); put('介', "jie");
            put('借', "jie"); put('阶', "jie"); put('街', "jie"); put('截', "jie"); put('届', "jie"); put('洁', "jie"); put('揭', "jie"); put('杰', "jie")
            put('进', "jin"); put('今', "jin"); put('金', "jin"); put('近', "jin"); put('尽', "jin"); put('仅', "jin"); put('紧', "jin"); put('禁', "jin"); put('劲', "jin"); put('津', "jin"); put('锦', "jin")
            put('经', "jing"); put('精', "jing"); put('京', "jing"); put('静', "jing"); put('景', "jing"); put('境', "jing"); put('竟', "jing"); put('警', "jing"); put('竞', "jing"); put('镜', "jing"); put('径', "jing"); put('净', "jing"); put('敬', "jing")
            put('九', "jiu"); put('就', "jiu"); put('旧', "jiu"); put('久', "jiu"); put('酒', "jiu"); put('救', "jiu"); put('纠', "jiu"); put('舅', "jiu"); put('究', "jiu")
            put('局', "ju"); put('举', "ju"); put('据', "ju"); put('句', "ju"); put('具', "ju"); put('剧', "ju"); put('聚', "ju"); put('拒', "ju"); put('距', "ju"); put('巨', "ju"); put('俱', "ju"); put('菊', "ju"); put('居', "ju")

            // juan - jue - jun
            put('卷', "juan"); put('捐', "juan"); put('倦', "juan")
            put('觉', "jue"); put('决', "jue"); put('绝', "jue"); put('掘', "jue"); put('爵', "jue"); put('崛', "jue"); put('角', "jue")
            put('军', "jun"); put('均', "jun"); put('俊', "jun"); put('郡', "jun"); put('君', "jun")

            // ka - kai - kan - kang - kao - ke - ken - keng - kong - kou - ku - kua - kuai - kuan - kuang - kui - kun - kuo
            put('卡', "ka"); put('咖', "ka")
            put('开', "kai"); put('凯', "kai"); put('慨', "kai")
            put('看', "kan"); put('砍', "kan"); put('刊', "kan"); put('堪', "kan"); put('勘', "kan")
            put('抗', "kang"); put('康', "kang"); put('扛', "kang"); put('慷', "kang"); put('炕', "kang")
            put('考', "kao"); put('靠', "kao"); put('烤', "kao"); put('拷', "kao")
            put('可', "ke"); put('课', "ke"); put('科', "ke"); put('刻', "ke"); put('客', "ke"); put('克', "ke"); put('颗', "ke"); put('壳', "ke"); put('渴', "ke"); put('咳', "ke"); put('柯', "ke")
            put('肯', "ken"); put('恳', "ken"); put('啃', "ken"); put('垦', "ken")
            put('坑', "keng")
            put('空', "kong"); put('控', "kong"); put('恐', "kong"); put('孔', "kong")
            put('口', "kou"); put('扣', "kou"); put('寇', "kou"); put('抠', "kou")
            put('苦', "ku"); put('哭', "ku"); put('库', "ku"); put('酷', "ku"); put('裤', "ku"); put('窟', "ku")
            put('跨', "kua"); put('夸', "kua"); put('垮', "kua")
            put('快', "kuai"); put('块', "kuai"); put('筷', "kuai")
            put('宽', "kuan"); put('款', "kuan")
            put('况', "kuang"); put('矿', "kuang"); put('狂', "kuang"); put('框', "kuang"); put('旷', "kuang")
            put('亏', "kui"); put('愧', "kui"); put('溃', "kui"); put('葵', "kui")
            put('困', "kun"); put('昆', "kun"); put('捆', "kun"); put('坤', "kun")
            put('扩', "kuo"); put('括', "kuo"); put('阔', "kuo")

            // la - lai - lan - lang - lao - le - lei - leng - li - lia - lian - liang - liao - lie - lin - ling - liu - long - lou - lu
            put('拉', "la"); put('啦', "la"); put('辣', "la"); put('蜡', "la"); put('腊', "la")
            put('来', "lai"); put('赖', "lai"); put('莱', "lai")
            put('蓝', "lan"); put('兰', "lan"); put('烂', "lan"); put('拦', "lan"); put('栏', "lan"); put('览', "lan"); put('懒', "lan"); put('篮', "lan")
            put('浪', "lang"); put('狼', "lang"); put('朗', "lang"); put('郎', "lang"); put('廊', "lang")
            put('老', "lao"); put('劳', "lao"); put('捞', "lao"); put('牢', "lao"); put('姥', "lao"); put('佬', "lao")
            put('了', "le"); put('乐', "le"); put('勒', "le")
            put('类', "lei"); put('累', "lei"); put('雷', "lei"); put('泪', "lei"); put('垒', "lei"); put('蕾', "lei")
            put('冷', "leng"); put('愣', "leng"); put('棱', "leng")
            put('里', "li"); put('力', "li"); put('利', "li"); put('理', "li"); put('李', "li"); put('立', "li")
            put('例', "li"); put('离', "li"); put('历', "li"); put('礼', "li"); put('粒', "li"); put('厉', "li"); put('丽', "li"); put('励', "li"); put('莉', "li"); put('黎', "li")
            put('俩', "lia")
            put('联', "lian"); put('连', "lian"); put('练', "lian"); put('脸', "lian"); put('恋', "lian"); put('链', "lian"); put('莲', "lian"); put('帘', "lian"); put('怜', "lian")
            put('两', "liang"); put('亮', "liang"); put('量', "liang"); put('凉', "liang"); put('梁', "liang"); put('良', "liang"); put('谅', "liang"); put('粮', "liang")
            put('了', "liao"); put('料', "liao"); put('聊', "liao"); put('疗', "liao"); put('辽', "liao"); put('僚', "liao")
            put('列', "lie"); put('烈', "lie"); put('裂', "lie"); put('猎', "lie"); put('劣', "lie")
            put('林', "lin"); put('临', "lin"); put('邻', "lin"); put('淋', "lin"); put('磷', "lin"); put('鳞', "lin")
            put('领', "ling"); put('另', "ling"); put('令', "ling"); put('灵', "ling"); put('零', "ling"); put('龄', "ling"); put('铃', "ling"); put('凌', "ling"); put('岭', "ling"); put('陵', "ling")
            put('流', "liu"); put('六', "liu"); put('留', "liu"); put('刘', "liu"); put('柳', "liu"); put('溜', "liu")
            put('龙', "long"); put('弄', "long"); put('笼', "long"); put('隆', "long"); put('聋', "long")
            put('楼', "lou"); put('漏', "lou"); put('露', "lou"); put('搂', "lou"); put('陋', "lou")
            put('路', "lu"); put('录', "lu"); put('陆', "lu"); put('露', "lu"); put('鲁', "lu"); put('炉', "lu"); put('鹿', "lu"); put('绿', "lu")

            // lü - luan - lüe - lun - luo
            put('旅', "lv"); put('绿', "lv"); put('律', "lv"); put('率', "lv"); put('滤', "lv"); put('吕', "lv"); put('铝', "lv"); put('屡', "lv"); put('履', "lv")
            put('乱', "luan"); put('卵', "luan"); put('峦', "luan")
            put('略', "lve"); put('掠', "lve")
            put('论', "lun"); put('轮', "lun"); put('伦', "lun"); put('沦', "lun"); put('纶', "lun")
            put('落', "luo"); put('罗', "luo"); put('络', "luo"); put('洛', "luo"); put('裸', "luo"); put('骆', "luo"); put('逻', "luo"); put('萝', "luo"); put('锣', "luo")

            // ma - mai - man - mang - mao - me - mei - men - meng - mi - mian - miao - mie - min - ming - miu - mo - mou - mu
            put('马', "ma"); put('吗', "ma"); put('妈', "ma"); put('麻', "ma"); put('骂', "ma"); put('码', "ma"); put('蚂', "ma")
            put('买', "mai"); put('卖', "mai"); put('麦', "mai"); put('埋', "mai"); put('迈', "mai")
            put('满', "man"); put('慢', "man"); put('忙', "man"); put('漫', "man"); put('蛮', "man"); put('瞒', "man"); put('馒', "man")
            put('忙', "mang"); put('盲', "mang"); put('芒', "mang"); put('茫', "mang"); put('蟒', "mang")
            put('猫', "mao"); put('毛', "mao"); put('冒', "mao"); put('帽', "mao"); put('矛', "mao"); put('茂', "mao"); put('贸', "mao")
            put('么', "me")
            put('没', "mei"); put('每', "mei"); put('美', "mei"); put('妹', "mei"); put('梅', "mei"); put('煤', "mei"); put('眉', "mei"); put('霉', "mei"); put('玫', "mei")
            put('门', "men"); put('们', "men"); put('闷', "men")
            put('梦', "meng"); put('猛', "meng"); put('蒙', "meng"); put('盟', "meng"); put('孟', "meng"); put('萌', "meng")
            put('米', "mi"); put('密', "mi"); put('迷', "mi"); put('秘', "mi"); put('蜜', "mi"); put('弥', "mi")
            put('面', "mian"); put('免', "mian"); put('棉', "mian"); put('眠', "mian"); put('绵', "mian")
            put('秒', "miao"); put('妙', "miao"); put('描', "miao"); put('庙', "miao"); put('苗', "miao")
            put('灭', "mie"); put('蔑', "mie")
            put('民', "min"); put('敏', "min"); put('闽', "min")
            put('名', "ming"); put('明', "ming"); put('命', "ming"); put('鸣', "ming"); put('铭', "ming")
            put('摸', "mo"); put('末', "mo"); put('魔', "mo"); put('莫', "mo"); put('默', "mo"); put('模', "mo"); put('摩', "mo"); put('墨', "mo"); put('磨', "mo"); put('膜', "mo"); put('漠', "mo")
            put('某', "mou"); put('谋', "mou")
            put('目', "mu"); put('木', "mu"); put('母', "mu"); put('幕', "mu"); put('牧', "mu"); put('墓', "mu"); put('慕', "mu"); put('暮', "mu"); put('姆', "mu"); put('亩', "mu"); put('穆', "mu")

            // na - nai - nan - nang - nao - ne - nei - nen - neng - ni - nian - niang - niao - nie - nin - ning - niu - nong - nu - nü - nuan - nuo
            put('那', "na"); put('拿', "na"); put('哪', "na"); put('纳', "na"); put('娜', "na")
            put('你', "ni"); put('尼', "ni"); put('泥', "ni"); put('逆', "ni"); put('拟', "ni"); put('腻', "ni")
            put('年', "nian"); put('念', "nian"); put('粘', "nian"); put('碾', "nian")
            put('娘', "niang"); put('酿', "niang")
            put('鸟', "niao"); put('尿', "niao")
            put('牛', "niu"); put('扭', "niu"); put('纽', "niu")
            put('农', "nong"); put('弄', "nong"); put('浓', "nong")
            put('女', "nv"); put('努', "nu"); put('怒', "nu")
            put('暖', "nuan")

            // ou
            put('偶', "ou"); put('欧', "ou"); put('殴', "ou"); put('呕', "ou"); put('藕', "ou")

            // pa - pai - pan - pang - pao - pei - pen - peng - pi - pian - piao - pie - pin - ping - po - pou - pu
            put('怕', "pa"); put('爬', "pa"); put('帕', "pa"); put('趴', "pa")
            put('拍', "pai"); put('排', "pai"); put('牌', "pai"); put('派', "pai")
            put('判', "pan"); put('盘', "pan"); put('盼', "pan"); put('攀', "pan")
            put('旁', "pang"); put('胖', "pang"); put('庞', "pang")
            put('跑', "pao"); put('泡', "pao"); put('炮', "pao"); put('抛', "pao")
            put('配', "pei"); put('培', "pei"); put('陪', "pei"); put('赔', "pei"); put('佩', "pei"); put('沛', "pei")
            put('喷', "pen"); put('盆', "pen")
            put('朋', "peng"); put('碰', "peng"); put('鹏', "peng"); put('捧', "peng"); put('蓬', "peng"); put('膨', "peng")
            put('批', "pi"); put('皮', "pi"); put('屁', "pi"); put('匹', "pi"); put('脾', "pi"); put('疲', "pi"); put('劈', "pi"); put('僻', "pi")
            put('片', "pian"); put('篇', "pian"); put('骗', "pian"); put('偏', "pian"); put('便', "pian")
            put('票', "piao"); put('飘', "piao"); put('漂', "piao")
            put('品', "pin"); put('拼', "pin"); put('贫', "pin"); put('频', "pin")
            put('平', "ping"); put('评', "ping"); put('瓶', "ping"); put('凭', "ping"); put('屏', "ping"); put('萍', "ping"); put('苹', "ping")
            put('破', "po"); put('迫', "po"); put('坡', "po"); put('泼', "po"); put('婆', "po"); put('颇', "po")
            put('普', "pu"); put('扑', "pu"); put('铺', "pu"); put('朴', "pu"); put('葡', "pu"); put('谱', "pu"); put('瀑', "pu")

            // qi - qia - qian - qiang - qiao - qie - qin - qing - qiong - qiu - qu - quan - que - qun
            put('七', "qi"); put('其', "qi"); put('起', "qi"); put('气', "qi"); put('期', "qi")
            put('奇', "qi"); put('齐', "qi"); put('器', "qi"); put('旗', "qi"); put('骑', "qi"); put('启', "qi"); put('企', "qi"); put('汽', "qi"); put('妻', "qi"); put('弃', "qi"); put('欺', "qi")
            put('恰', "qia"); put('掐', "qia")
            put('前', "qian"); put('钱', "qian"); put('千', "qian"); put('签', "qian"); put('欠', "qian"); put('浅', "qian"); put('潜', "qian"); put('迁', "qian"); put('谦', "qian"); put('嵌', "qian")
            put('强', "qiang"); put('抢', "qiang"); put('墙', "qiang"); put('枪', "qiang"); put('腔', "qiang")
            put('桥', "qiao"); put('巧', "qiao"); put('敲', "qiao"); put('瞧', "qiao"); put('翘', "qiao"); put('悄', "qiao")
            put('且', "qie"); put('切', "qie"); put('窃', "qie"); put('怯', "qie")
            put('亲', "qin"); put('勤', "qin"); put('侵', "qin"); put('琴', "qin"); put('禽', "qin"); put('秦', "qin"); put('寝', "qin")
            put('请', "qing"); put('清', "qing"); put('情', "qing"); put('青', "qing"); put('轻', "qing"); put('庆', "qing"); put('晴', "qing"); put('倾', "qing"); put('卿', "qing")
            put('球', "qiu"); put('求', "qiu"); put('秋', "qiu"); put('丘', "qiu"); put('囚', "qiu")
            put('取', "qu"); put('去', "qu"); put('区', "qu"); put('曲', "qu"); put('趣', "qu"); put('驱', "qu"); put('屈', "qu"); put('渠', "qu"); put('趋', "qu")
            put('全', "quan"); put('权', "quan"); put('圈', "quan"); put('劝', "quan"); put('泉', "quan"); put('拳', "quan"); put('犬', "quan")
            put('却', "que"); put('确', "que"); put('缺', "que"); put('雀', "que"); put('鹊', "que")
            put('群', "qun"); put('裙', "qun")

            // ran - rang - rao - re - ren - reng - ri - rong - rou - ru - ruan - rui - run - ruo
            put('然', "ran"); put('染', "ran"); put('燃', "ran")
            put('让', "rang"); put('壤', "rang"); put('嚷', "rang")
            put('绕', "rao"); put('扰', "rao"); put('饶', "rao")
            put('热', "re"); put('惹', "re")
            put('人', "ren"); put('任', "ren"); put('认', "ren"); put('忍', "ren"); put('仁', "ren"); put('刃', "ren")
            put('仍', "reng"); put('扔', "reng")
            put('日', "ri")
            put('容', "rong"); put('融', "rong"); put('荣', "rong"); put('熔', "rong"); put('蓉', "rong"); put('绒', "rong"); put('冗', "rong")
            put('肉', "rou"); put('柔', "rou"); put('揉', "rou")
            put('如', "ru"); put('入', "ru"); put('乳', "ru"); put('儒', "ru"); put('辱', "ru")
            put('软', "ruan")
            put('瑞', "rui"); put('锐', "rui"); put('睿', "rui")
            put('润', "run"); put('闰', "run")
            put('若', "ruo"); put('弱', "ruo")

            // sa - sai - san - sang - sao - se - sen - seng - sha - shai - shan - shang - shao - she - shen - sheng - shi
            put('洒', "sa"); put('萨', "sa")
            put('三', "san"); put('散', "san"); put('伞', "san")
            put('扫', "sao"); put('嫂', "sao"); put('骚', "sao")
            put('色', "se"); put('瑟', "se"); put('涩', "se")
            put('沙', "sha"); put('杀', "sha"); put('傻', "sha"); put('纱', "sha"); put('砂', "sha")
            put('山', "shan"); put('闪', "shan"); put('善', "shan"); put('衫', "shan"); put('删', "shan"); put('扇', "shan"); put('陕', "shan")
            put('上', "shang"); put('伤', "shang"); put('商', "shang"); put('赏', "shang"); put('尚', "shang"); put('裳', "shang")
            put('少', "shao"); put('绍', "shao"); put('烧', "shao"); put('稍', "shao"); put('勺', "shao"); put('哨', "shao")
            put('社', "she"); put('设', "she"); put('射', "she"); put('涉', "she"); put('蛇', "she"); put('舍', "she"); put('摄', "she"); put('奢', "she")
            put('什', "shen"); put('身', "shen"); put('深', "shen"); put('神', "shen"); put('甚', "shen"); put('审', "shen"); put('伸', "shen"); put('沈', "shen"); put('慎', "shen"); put('渗', "shen")
            put('生', "sheng"); put('声', "sheng"); put('省', "sheng"); put('升', "sheng"); put('胜', "sheng"); put('剩', "sheng"); put('圣', "sheng"); put('盛', "sheng"); put('绳', "sheng")
            put('是', "shi"); put('时', "shi"); put('十', "shi"); put('事', "shi"); put('实', "shi"); put('使', "shi"); put('世', "shi"); put('市', "shi"); put('式', "shi"); put('石', "shi");
            put('师', "shi"); put('识', "shi"); put('始', "shi"); put('士', "shi"); put('失', "shi"); put('试', "shi"); put('史', "shi"); put('示', "shi"); put('适', "shi");
            put('施', "shi"); put('室', "shi"); put('食', "shi"); put('视', "shi"); put('势', "shi"); put('诗', "shi"); put('湿', "shi"); put('释', "shi"); put('氏', "shi")

            // shou - shu - shua - shuai - shuan - shuang - shui - shun - shuo
            put('手', "shou"); put('受', "shou"); put('首', "shou"); put('守', "shou"); put('收', "shou"); put('售', "shou"); put('授', "shou"); put('瘦', "shou"); put('兽', "shou")
            put('书', "shu"); put('数', "shu"); put('术', "shu"); put('树', "shu"); put('输', "shu"); put('属', "shu"); put('述', "shu"); put('束', "shu"); put('熟', "shu"); put('鼠', "shu"); put('竖', "shu"); put('舒', "shu"); put('疏', "shu"); put('署', "shu"); put('薯', "shu")
            put('刷', "shua"); put('耍', "shua")
            put('摔', "shuai"); put('甩', "shuai"); put('帅', "shuai"); put('衰', "shuai")
            put('双', "shuang"); put('爽', "shuang"); put('霜', "shuang")
            put('水', "shui"); put('睡', "shui"); put('税', "shui"); put('谁', "shui")
            put('顺', "shun"); put('瞬', "shun"); put('纯', "shun")
            put('说', "shuo"); put('硕', "shuo"); put('烁', "shuo")

            // si - song - sou - su - suan - sui - sun - suo
            put('四', "si"); put('思', "si"); put('死', "si"); put('丝', "si"); put('私', "si"); put('似', "si"); put('撕', "si"); put('寺', "si"); put('司', "si"); put('饲', "si")
            put('送', "song"); put('松', "song"); put('宋', "song"); put('诵', "song"); put('耸', "song")
            put('搜', "sou"); put('艘', "sou"); put('嗽', "sou")
            put('素', "su"); put('速', "su"); put('苏', "su"); put('诉', "su"); put('俗', "su"); put('肃', "su"); put('宿', "su"); put('塑', "su"); put('缩', "su")
            put('算', "suan"); put('酸', "suan"); put('蒜', "suan")
            put('岁', "sui"); put('随', "sui"); put('虽', "sui"); put('碎', "sui"); put('遂', "sui"); put('穗', "sui")
            put('损', "sun"); put('孙', "sun"); put('笋', "sun")
            put('所', "suo"); put('索', "suo"); put('锁', "suo"); put('缩', "suo"); put('梭', "suo")

            // ta - tai - tan - tang - tao - te - teng - ti - tian - tiao - tie - ting - tong - tou - tu - tuan - tui - tun - tuo
            put('他', "ta"); put('她', "ta"); put('它', "ta"); put('塔', "ta"); put('踏', "ta"); put('塌', "ta")
            put('太', "tai"); put('台', "tai"); put('态', "tai"); put('抬', "tai"); put('泰', "tai"); put('胎', "tai")
            put('谈', "tan"); put('弹', "tan"); put('探', "tan"); put('坦', "tan"); put('叹', "tan"); put('摊', "tan"); put('贪', "tan"); put('滩', "tan"); put('碳', "tan")
            put('唐', "tang"); put('堂', "tang"); put('糖', "tang"); put('躺', "tang"); put('汤', "tang"); put('趟', "tang"); put('塘', "tang"); put('倘', "tang")
            put('套', "tao"); put('讨', "tao"); put('逃', "tao"); put('桃', "tao"); put('淘', "tao"); put('陶', "tao")
            put('特', "te")
            put('疼', "teng"); put('腾', "teng"); put('藤', "teng")
            put('提', "ti"); put('题', "ti"); put('体', "ti"); put('替', "ti"); put('踢', "ti"); put('梯', "ti"); put('剔', "ti")
            put('天', "tian"); put('田', "tian"); put('填', "tian"); put('甜', "tian"); put('添', "tian")
            put('条', "tiao"); put('调', "tiao"); put('跳', "tiao"); put('挑', "tiao"); put('贴', "tie"); put('铁', "tie")
            put('听', "ting"); put('停', "ting"); put('庭', "ting"); put('厅', "ting"); put('挺', "ting"); put('亭', "ting")
            put('同', "tong"); put('通', "tong"); put('痛', "tong"); put('统', "tong"); put('铜', "tong"); put('童', "tong"); put('筒', "tong"); put('桶', "tong"); put('桐', "tong")
            put('头', "tou"); put('投', "tou"); put('透', "tou"); put('偷', "tou")
            put('图', "tu"); put('土', "tu"); put('突', "tu"); put('途', "tu"); put('涂', "tu"); put('吐', "tu"); put('兔', "tu"); put('屠', "tu")
            put('团', "tuan")
            put('推', "tui"); put('退', "tui"); put('腿', "tui"); put('颓', "tui")
            put('吞', "tun"); put('屯', "tun")
            put('脱', "tuo"); put('拖', "tuo"); put('托', "tuo"); put('妥', "tuo"); put('拓', "tuo"); put('驼', "tuo"); put('椭', "tuo")

            // wa - wai - wan - wang - wei - wen - weng - wo - wu
            put('瓦', "wa"); put('挖', "wa"); put('娃', "wa"); put('袜', "wa")
            put('外', "wai"); put('歪', "wai")
            put('完', "wan"); put('万', "wan"); put('晚', "wan"); put('玩', "wan"); put('湾', "wan"); put('碗', "wan"); put('弯', "wan"); put('挽', "wan"); put('顽', "wan")
            put('王', "wang"); put('往', "wang"); put('望', "wang"); put('网', "wang"); put('忘', "wang"); put('亡', "wang"); put('汪', "wang"); put('旺', "wang"); put('枉', "wang")
            put('为', "wei"); put('位', "wei"); put('未', "wei"); put('委', "wei"); put('围', "wei"); put('微', "wei"); put('维', "wei"); put('味', "wei");
            put('卫', "wei"); put('伟', "wei"); put('威', "wei"); put('危', "wei"); put('唯', "wei"); put('谓', "wei"); put('慰', "wei"); put('胃', "wei"); put('尾', "wei"); put('违', "wei"); put('魏', "wei")
            put('文', "wen"); put('问', "wen"); put('闻', "wen"); put('温', "wen"); put('稳', "wen"); put('纹', "wen"); put('吻', "wen")
            put('翁', "weng")
            put('我', "wo"); put('握', "wo"); put('窝', "wo"); put('卧', "wo"); put('涡', "wo"); put('沃', "wo")
            put('五', "wu"); put('无', "wu"); put('物', "wu"); put('屋', "wu"); put('舞', "wu"); put('务', "wu"); put('误', "wu"); put('雾', "wu"); put('午', "wu"); put('吴', "wu"); put('武', "wu"); put('污', "wu"); put('乌', "wu"); put('勿', "wu"); put('悟', "wu")

            // xi - xia - xian - xiang - xiao - xie - xin - xing - xiong - xiu - xu - xuan - xue - xun
            put('西', "xi"); put('系', "xi"); put('细', "xi"); put('喜', "xi"); put('席', "xi"); put('习', "xi"); put('戏', "xi"); put('吸', "xi"); put('析', "xi"); put('希', "xi"); put('息', "xi"); put('洗', "xi"); put('稀', "xi"); put('析', "xi")
            put('下', "xia"); put('夏', "xia"); put('吓', "xia"); put('虾', "xia"); put('峡', "xia"); put('霞', "xia"); put('狭', "xia")
            put('先', "xian"); put('现', "xian"); put('线', "xian"); put('显', "xian"); put('限', "xian"); put('险', "xian"); put('献', "xian"); put('鲜', "xian"); put('县', "xian"); put('闲', "xian"); put('陷', "xian"); put('纤', "xian"); put('宪', "xian"); put('嫌', "xian"); put('贤', "xian")
            put('想', "xiang"); put('向', "xiang"); put('像', "xiang"); put('相', "xiang"); put('响', "xiang"); put('项', "xiang"); put('香', "xiang"); put('乡', "xiang"); put('箱', "xiang"); put('享', "xiang"); put('详', "xiang"); put('祥', "xiang"); put('象', "xiang")
            put('小', "xiao"); put('笑', "xiao"); put('消', "xiao"); put('效', "xiao"); put('校', "xiao"); put('晓', "xiao"); put('销', "xiao"); put('萧', "xiao"); put('孝', "xiao"); put('肖', "xiao")
            put('些', "xie"); put('写', "xie"); put('谢', "xie"); put('鞋', "xie"); put('协', "xie"); put('血', "xie"); put('械', "xie"); put('斜', "xie"); put('携', "xie"); put('泄', "xie"); put('歇', "xie"); put('胁', "xie")
            put('新', "xin"); put('心', "xin"); put('信', "xin"); put('辛', "xin"); put('欣', "xin"); put('芯', "xin"); put('薪', "xin")
            put('行', "xing"); put('性', "xing"); put('形', "xing"); put('星', "xing"); put('型', "xing"); put('醒', "xing"); put('幸', "xing"); put('兴', "xing"); put('姓', "xing"); put('刑', "xing"); put('杏', "xing"); put('腥', "xing")
            put('胸', "xiong"); put('兄', "xiong"); put('凶', "xiong"); put('雄', "xiong"); put('熊', "xiong"); put('匈', "xiong")
            put('休', "xiu"); put('修', "xiu"); put('秀', "xiu"); put('袖', "xiu"); put('羞', "xiu"); put('锈', "xiu"); put('嗅', "xiu")
            put('需', "xu"); put('许', "xu"); put('续', "xu"); put('须', "xu"); put('序', "xu"); put('虚', "xu"); put('畜', "xu"); put('蓄', "xu"); put('叙', "xu"); put('绪', "xu"); put('徐', "xu"); put('旭', "xu")
            put('选', "xuan"); put('宣', "xuan"); put('旋', "xuan"); put('悬', "xuan"); put('玄', "xuan"); put('炫', "xuan")
            put('学', "xue"); put('血', "xue"); put('雪', "xue"); put('穴', "xue"); put('削', "xue")
            put('寻', "xun"); put('训', "xun"); put('迅', "xun"); put('询', "xun"); put('巡', "xun"); put('旬', "xun"); put('循', "xun"); put('讯', "xun"); put('逊', "xun")

            // ya - yan - yang - yao - ye - yi - yin - ying - yo - yong - you
            put('压', "ya"); put('呀', "ya"); put('牙', "ya"); put('芽', "ya"); put('鸭', "ya"); put('雅', "ya"); put('亚', "ya"); put('押', "ya")
            put('眼', "yan"); put('言', "yan"); put('演', "yan"); put('严', "yan"); put('烟', "yan"); put('验', "yan"); put('研', "yan"); put('沿', "yan");
            put('盐', "yan"); put('颜', "yan"); put('延', "yan"); put('岩', "yan"); put('掩', "yan"); put('宴', "yan"); put('厌', "yan"); put('艳', "yan"); put('燕', "yan"); put('炎', "yan"); put('咽', "yan")
            put('样', "yang"); put('阳', "yang"); put('央', "yang"); put('养', "yang"); put('洋', "yang"); put('扬', "yang"); put('杨', "yang"); put('仰', "yang"); put('痒', "yang"); put('羊', "yang")
            put('要', "yao"); put('药', "yao"); put('摇', "yao"); put('腰', "yao"); put('咬', "yao"); put('邀', "yao"); put('遥', "yao")
            put('也', "ye"); put('业', "ye"); put('夜', "ye"); put('爷', "ye"); put('页', "ye"); put('野', "ye"); put('液', "ye"); put('叶', "ye"); put('冶', "ye")
            put('一', "yi"); put('以', "yi"); put('已', "yi"); put('意', "yi"); put('义', "yi"); put('议', "yi"); put('衣', "yi"); put('易', "yi"); put('异', "yi");
            put('医', "yi"); put('移', "yi"); put('益', "yi"); put('仪', "yi"); put('艺', "yi"); put('疑', "yi"); put('遗', "yi"); put('忆', "yi"); put('宜', "yi"); put('译', "yi"); put('亿', "yi"); put('翼', "yi"); put('毅', "yi")
            put('因', "yin"); put('引', "yin"); put('印', "yin"); put('音', "yin"); put('银', "yin"); put('阴', "yin"); put('饮', "yin"); put('隐', "yin"); put('吟', "yin")
            put('应', "ying"); put('影', "ying"); put('英', "ying"); put('营', "ying"); put('迎', "ying"); put('硬', "ying"); put('映', "ying"); put('赢', "ying"); put('盈', "ying"); put('颖', "ying"); put('鹰', "ying"); put('樱', "ying")
            put('用', "yong"); put('永', "yong"); put('勇', "yong"); put('拥', "yong"); put('泳', "yong"); put('涌', "yong"); put('庸', "yong"); put('咏', "yong")
            put('有', "you"); put('又', "you"); put('由', "you"); put('游', "you"); put('右', "you"); put('友', "you"); put('优', "you"); put('油', "you"); put('邮', "you"); put('尤', "you"); put('犹', "you"); put('幼', "you"); put('幽', "you"); put('悠', "you")

            // yu - yuan - yue - yun
            put('于', "yu"); put('与', "yu"); put('语', "yu"); put('雨', "yu"); put('鱼', "yu"); put('预', "yu"); put('余', "yu"); put('遇', "yu"); put('域', "yu");
            put('育', "yu"); put('玉', "yu"); put('欲', "yu"); put('愈', "yu"); put('渔', "yu"); put('宇', "yu"); put('娱', "yu"); put('裕', "yu"); put('舆', "yu"); put('羽', "yu"); put('狱', "yu")
            put('原', "yuan"); put('远', "yuan"); put('元', "yuan"); put('愿', "yuan"); put('院', "yuan"); put('员', "yuan"); put('圆', "yuan"); put('源', "yuan"); put('缘', "yuan"); put('援', "yuan"); put('怨', "yuan"); put('园', "yuan")
            put('月', "yue"); put('越', "yue"); put('阅', "yue"); put('约', "yue"); put('乐', "yue"); put('跃', "yue"); put('悦', "yue")
            put('运', "yun"); put('云', "yun"); put('允', "yun"); put('孕', "yun"); put('晕', "yun"); put('韵', "yun"); put('匀', "yun")

            // za - zai - zan - zang - zao - ze - zei - zen - zeng - zha - zhai - zhan - zhang - zhao - zhe
            put('杂', "za"); put('砸', "za")
            put('在', "zai"); put('再', "zai"); put('灾', "zai"); put('载', "zai"); put('栽', "zai"); put('宰', "zai")
            put('咱', "zan"); put('赞', "zan"); put('攒', "zan"); put('暂', "zan")
            put('脏', "zang"); put('葬', "zang")
            put('早', "zao"); put('造', "zao"); put('遭', "zao"); put('糟', "zao"); put('灶', "zao"); put('燥', "zao"); put('凿', "zao"); put('枣', "zao"); put('噪', "zao")
            put('则', "ze"); put('责', "ze"); put('择', "ze"); put('泽', "ze")
            put('贼', "zei")
            put('怎', "zen"); put('曾', "zeng"); put('增', "zeng"); put('赠', "zeng")
            put('咋', "zha")
            put('炸', "zha"); put('扎', "zha"); put('眨', "zha"); put('渣', "zha"); put('闸', "zha")
            put('展', "zhan"); put('站', "zhan"); put('占', "zhan"); put('战', "zhan"); put('粘', "zhan"); put('盏', "zhan"); put('斩', "zhan"); put('崭', "zhan")
            put('张', "zhang"); put('长', "zhang"); put('章', "zhang"); put('掌', "zhang"); put('丈', "zhang"); put('障', "zhang"); put('涨', "zhang"); put('仗', "zhang"); put('账', "zhang")
            put('找', "zhao"); put('照', "zhao"); put('赵', "zhao"); put('招', "zhao"); put('罩', "zhao"); put('兆', "zhao"); put('召', "zhao"); put('爪', "zhao")
            put('者', "zhe"); put('这', "zhe"); put('着', "zhe"); put('折', "zhe"); put('哲', "zhe"); put('浙', "zhe"); put('遮', "zhe")

            // zhen - zheng - zhi - zhong - zhou - zhu - zhua - zhuai - zhuan - zhuang - zhui - zhun - zhuo
            put('真', "zhen"); put('阵', "zhen"); put('针', "zhen"); put('镇', "zhen"); put('震', "zhen"); put('振', "zhen"); put('诊', "zhen"); put('枕', "zhen"); put('贞', "zhen")
            put('正', "zheng"); put('整', "zheng"); put('政', "zheng"); put('证', "zheng"); put('争', "zheng"); put('征', "zheng"); put('郑', "zheng")
            put('知', "zhi"); put('之', "zhi"); put('只', "zhi"); put('制', "zhi"); put('指', "zhi"); put('直', "zhi"); put('治', "zhi")
            put('至', "zhi"); put('值', "zhi"); put('质', "zhi"); put('职', "zhi"); put('志', "zhi"); put('支', "zhi"); put('止', "zhi"); put('纸', "zhi")
            put('智', "zhi"); put('致', "zhi"); put('置', "zhi"); put('执', "zhi"); put('植', "zhi"); put('织', "zhi"); put('芝', "zhi")
            put('中', "zhong"); put('种', "zhong"); put('众', "zhong"); put('终', "zhong"); put('钟', "zhong"); put('忠', "zhong"); put('肿', "zhong"); put('仲', "zhong")
            put('周', "zhou"); put('州', "zhou"); put('洲', "zhou"); put('轴', "zhou"); put('粥', "zhou"); put('皱', "zhou"); put('骤', "zhou"); put('宙', "zhou")
            put('主', "zhu"); put('住', "zhu"); put('注', "zhu"); put('助', "zhu"); put('朱', "zhu"); put('珠', "zhu"); put('祝', "zhu"); put('猪', "zhu"); put('筑', "zhu"); put('逐', "zhu"); put('竹', "zhu"); put('烛', "zhu"); put('煮', "zhu"); put('著', "zhu")
            put('抓', "zhua")
            put('转', "zhuan"); put('专', "zhuan"); put('砖', "zhuan"); put('赚', "zhuan")
            put('装', "zhuang"); put('状', "zhuang"); put('壮', "zhuang"); put('庄', "zhuang"); put('撞', "zhuang"); put('妆', "zhuang")
            put('追', "zhui"); put('坠', "zhui"); put('缀', "zhui")
            put('准', "zhun")
            put('捉', "zhuo"); put('桌', "zhuo"); put('浊', "zhuo"); put('啄', "zhuo"); put('卓', "zhuo"); put('着', "zhuo")
        }

        fun toSinglePinyin(ch: Char): String? {
            return PINYIN_MAP[ch]
        }
    }
}
