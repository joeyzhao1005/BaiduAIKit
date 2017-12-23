package com.kit.baiduai.enmus

/**
 *
 *
领域名称	描述	示例 query
weather	天气	后天天气如何；明天几度
calendar 日历	日历	到国庆节还有几天
train	列车	早上 8 点前发车去无锡的动车
flight	航班	飞上海最晚的国航航班
map	地图	西二旗到新街口坐几号线
telephone	电话	打电话给李四；查看有没有张三的未接来电
contact	联系人	找老爸的办公电话
message	短信	发短信给小明说下午 3 点文档评审
app	手机应用	打开相机；安装百度手机助手
website	网站	打开百度
alarm	提醒	提醒我周五晚上看中国好声音；工作日早上 8 点半叫醒我
sns	社交网络	发新浪微博，内容是我结婚了
setting	手机设置	打开 wifi
music	音乐	听刘德华的歌曲
joke	笑话	讲一个笑话
story	故事	讲一个故事
hotel	酒店	外滩附近速八酒店，商务大床
travel	旅游	豫园开放时间、门票？
instruction	通用指令	打开，关闭
video	视频 （现在只包含影视）	观看电影阿甘正传
translation	翻译	hello 的中文翻译
phone_charges	话费流量查询	我这个月的话费
tv_show	电视节目	我想看快乐大本营
person	人物	赵本山的女儿叫什么
tv_instruction	电视指令	回看刚才的节目
stock	股票	招商银行股票多少钱
novel	小说	我想看小说
player	播放器	继续播放
account	记账	昨天吃饭花了 10 块钱
search	搜索	搜索步步惊心
vehicle_instruction	车载指令	查看胎压
radio	收音机控制指令	收听 FM2.1
recipe	菜谱	西红柿炒鸡蛋怎么做
navigate_instruction	导航指令	报告超速
movie_news	影讯	最近有啥热门电影

 * Created by Zhao on 2017/12/23.
 */
enum class BDAIDomain {
    WEATHER,
    CALENDAR,
    TRAIN,
    FLIGHT
}