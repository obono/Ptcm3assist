#!/bin/perl

# code ranges supported by ptcm font
@char_range = (
	0x0000, 0x0000, 0x0009, 0x000A, 0x000D, 0x000D, 0x0020, 0x007F,
	0x00A0, 0x017F, 0x0192, 0x0192, 0x01C5, 0x01C6, 0x01F0, 0x01F0,
	0x01F2, 0x01F3, 0x021A, 0x021B, 0x02C7, 0x02C7, 0x02D8, 0x02DC,
	0x037E, 0x037E, 0x0384, 0x038A, 0x038C, 0x038C, 0x038E, 0x03A1,
	0x03A3, 0x03CE, 0x0401, 0x0401, 0x0410, 0x044F, 0x0451, 0x0451,
	0x2013, 0x2014, 0x2018, 0x201E, 0x2020, 0x2021, 0x2026, 0x2026,
	0x2030, 0x2030, 0x2032, 0x2033, 0x2039, 0x203B, 0x20A9, 0x20A9,
	0x20AC, 0x20AC, 0x2116, 0x2116, 0x2122, 0x2122, 0x2190, 0x2193,
	0x21D2, 0x21D2, 0x21D4, 0x21D4, 0x2200, 0x2200, 0x2202, 0x2202,
	0x221A, 0x221A, 0x221E, 0x221E, 0x2234, 0x2235, 0x2282, 0x2283,
	0x2312, 0x2312, 0x2500, 0x2503, 0x250C, 0x250C, 0x250F, 0x2510,
	0x2513, 0x2514, 0x2517, 0x2518, 0x251B, 0x251D, 0x2520, 0x2520,
	0x2523, 0x2525, 0x252B, 0x252C, 0x252F, 0x2530, 0x2533, 0x2534,
	0x2537, 0x2538, 0x253B, 0x253C, 0x253F, 0x253F, 0x2542, 0x2542,
	0x254B, 0x254B, 0x2574, 0x257B, 0x25A0, 0x25A1, 0x25B2, 0x25B3,
	0x25BC, 0x25BD, 0x25C6, 0x25C7, 0x25CB, 0x25CB, 0x25CE, 0x25CF,
	0x2605, 0x2606, 0x2640, 0x2640, 0x2642, 0x2642, 0x2660, 0x2667,
	0x266A, 0x266A, 0x266D, 0x266D, 0x3000, 0x3003, 0x3005, 0x3006,
	0x3008, 0x3012, 0x301C, 0x301D, 0x301F, 0x301F, 0x3041, 0x308F,
	0x3092, 0x3093, 0x309B, 0x309E, 0x30A1, 0x30EF, 0x30F2, 0x30F6,
	0x30FB, 0x30FC, 0x4EDD, 0x4EDD, 0xE100, 0xE17F, 0xE200, 0xE25A,
	0xE260, 0xE2C9, 0xE2E0, 0xE2FF, 0xFF00, 0xFF5E,
);

# codes supported by misaki font
@kanji = (
	0x4E00, 0x4E01, 0x4E03, 0x4E07, 0x4E08, 0x4E09, 0x4E0A, 0x4E0B, 0x4E0D,
	0x4E0E, 0x4E11, 0x4E14, 0x4E16, 0x4E18, 0x4E19, 0x4E1E, 0x4E21, 0x4E26,
	0x4E2D, 0x4E32, 0x4E38, 0x4E39, 0x4E3B, 0x4E43, 0x4E45, 0x4E4B, 0x4E4D,
	0x4E4E, 0x4E4F, 0x4E57, 0x4E59, 0x4E5D, 0x4E5E, 0x4E5F, 0x4E71, 0x4E73,
	0x4E7E, 0x4E80, 0x4E86, 0x4E88, 0x4E89, 0x4E8B, 0x4E8C, 0x4E91, 0x4E92,
	0x4E94, 0x4E95, 0x4E98, 0x4E99, 0x4E9B, 0x4E9C, 0x4EA1, 0x4EA4, 0x4EA5,
	0x4EA6, 0x4EA8, 0x4EAB, 0x4EAC, 0x4EAD, 0x4EAE, 0x4EBA, 0x4EC0, 0x4EC1,
	0x4EC7, 0x4ECA, 0x4ECB, 0x4ECF, 0x4ED4, 0x4ED5, 0x4ED6, 0x4ED8, 0x4ED9,
	0x4EE3, 0x4EE4, 0x4EE5, 0x4EEE, 0x4EF0, 0x4EF2, 0x4EF6, 0x4EFB, 0x4F01,
	0x4F0A, 0x4F0D, 0x4F0E, 0x4F0F, 0x4F10, 0x4F11, 0x4F1A, 0x4F1D, 0x4F2F,
	0x4F34, 0x4F36, 0x4F38, 0x4F3A, 0x4F3C, 0x4F3D, 0x4F43, 0x4F46, 0x4F4D,
	0x4F4E, 0x4F4F, 0x4F50, 0x4F51, 0x4F53, 0x4F55, 0x4F59, 0x4F5C, 0x4F73,
	0x4F75, 0x4F7C, 0x4F7F, 0x4F83, 0x4F8B, 0x4F8D, 0x4F9B, 0x4F9D, 0x4FA0,
	0x4FA1, 0x4FAD, 0x4FAE, 0x4FAF, 0x4FB5, 0x4FB6, 0x4FBF, 0x4FC2, 0x4FC3,
	0x4FC4, 0x4FCA, 0x4FD7, 0x4FDD, 0x4FE1, 0x4FE3, 0x4FEE, 0x4FF3, 0x4FF5,
	0x4FF8, 0x4FFA, 0x5009, 0x500B, 0x500D, 0x5012, 0x5016, 0x5019, 0x501F,
	0x5023, 0x5024, 0x5026, 0x502B, 0x502D, 0x5036, 0x5039, 0x5049, 0x504F,
	0x505C, 0x5065, 0x5072, 0x5074, 0x5075, 0x5076, 0x507D, 0x508D, 0x5091,
	0x5098, 0x5099, 0x50AC, 0x50AD, 0x50B5, 0x50B7, 0x50BE, 0x50C5, 0x50CD,
	0x50CF, 0x50D1, 0x50D5, 0x50DA, 0x50E7, 0x50FB, 0x5100, 0x5104, 0x5112,
	0x511F, 0x512A, 0x5132, 0x5141, 0x5143, 0x5144, 0x5145, 0x5146, 0x5147,
	0x5148, 0x5149, 0x514B, 0x514D, 0x514E, 0x5150, 0x515A, 0x515C, 0x5165,
	0x5168, 0x516B, 0x516C, 0x516D, 0x5171, 0x5175, 0x5176, 0x5177, 0x5178,
	0x517C, 0x5185, 0x5186, 0x518A, 0x518D, 0x5192, 0x5197, 0x5199, 0x51A0,
	0x51A5, 0x51A8, 0x51AC, 0x51B4, 0x51B6, 0x51B7, 0x51C4, 0x51C6, 0x51CB,
	0x51CC, 0x51CD, 0x51DD, 0x51E1, 0x51E6, 0x51E7, 0x51EA, 0x51F1, 0x51F6,
	0x51F8, 0x51F9, 0x51FA, 0x51FD, 0x5200, 0x5203, 0x5206, 0x5207, 0x5208,
	0x520A, 0x5211, 0x5217, 0x521D, 0x5224, 0x5225, 0x5229, 0x5230, 0x5236,
	0x5237, 0x5238, 0x523A, 0x523B, 0x5243, 0x5247, 0x524A, 0x524D, 0x5256,
	0x525B, 0x5263, 0x5264, 0x5265, 0x526F, 0x5270, 0x5272, 0x5275, 0x5283,
	0x5287, 0x5289, 0x529B, 0x529F, 0x52A0, 0x52A3, 0x52A9, 0x52AA, 0x52AB,
	0x52B1, 0x52B4, 0x52B9, 0x52BE, 0x52C3, 0x52C5, 0x52C7, 0x52C9, 0x52D5,
	0x52D8, 0x52D9, 0x52DD, 0x52DF, 0x52E2, 0x52E4, 0x52E7, 0x52F2, 0x52FA,
	0x52FE, 0x52FF, 0x5301, 0x5302, 0x5305, 0x5316, 0x5317, 0x5319, 0x531D,
	0x5320, 0x5321, 0x532A, 0x5339, 0x533A, 0x533B, 0x533F, 0x5341, 0x5343,
	0x5347, 0x5348, 0x534A, 0x5351, 0x5352, 0x5353, 0x5354, 0x5357, 0x5358,
	0x535A, 0x535C, 0x5360, 0x5366, 0x536F, 0x5370, 0x5371, 0x5373, 0x5374,
	0x5375, 0x5378, 0x537F, 0x5384, 0x5398, 0x539A, 0x539F, 0x53A8, 0x53A9,
	0x53AD, 0x53B3, 0x53BB, 0x53C2, 0x53C8, 0x53C9, 0x53CA, 0x53CB, 0x53CC,
	0x53CD, 0x53CE, 0x53D4, 0x53D6, 0x53D7, 0x53D9, 0x53DB, 0x53E1, 0x53E2,
	0x53E3, 0x53E4, 0x53E5, 0x53E9, 0x53EA, 0x53EB, 0x53EC, 0x53EF, 0x53F0,
	0x53F1, 0x53F2, 0x53F3, 0x53F6, 0x53F7, 0x53F8, 0x5403, 0x5404, 0x5408,
	0x5409, 0x540A, 0x540B, 0x540C, 0x540D, 0x540E, 0x540F, 0x5410, 0x5411,
	0x541B, 0x541F, 0x5420, 0x5426, 0x542B, 0x5438, 0x5439, 0x543B, 0x543E,
	0x5442, 0x5446, 0x5448, 0x5449, 0x544A, 0x5451, 0x5468, 0x546A, 0x5473,
	0x547C, 0x547D, 0x548B, 0x548C, 0x54B2, 0x54B3, 0x54BD, 0x54C0, 0x54C1,
	0x54C9, 0x54E1, 0x54E8, 0x54E9, 0x54F2, 0x5504, 0x5506, 0x5507, 0x5510,
	0x5516, 0x552F, 0x5531, 0x553E, 0x5544, 0x5546, 0x554F, 0x5553, 0x5584,
	0x5589, 0x558B, 0x559A, 0x559C, 0x559D, 0x55A7, 0x55AA, 0x55AB, 0x55AC,
	0x55B0, 0x55B6, 0x55E3, 0x5606, 0x5609, 0x5617, 0x5618, 0x5629, 0x5631,
	0x5642, 0x564C, 0x565B, 0x5668, 0x5674, 0x5678, 0x567A, 0x5687, 0x56A2,
	0x56DA, 0x56DB, 0x56DE, 0x56E0, 0x56E3, 0x56F0, 0x56F2, 0x56F3, 0x56FA,
	0x56FD, 0x5703, 0x570F, 0x5712, 0x571F, 0x5727, 0x5728, 0x572D, 0x5730,
	0x5742, 0x5747, 0x574A, 0x5750, 0x5751, 0x5764, 0x5766, 0x576A, 0x5782,
	0x578B, 0x57A2, 0x57A3, 0x57CB, 0x57CE, 0x57DC, 0x57DF, 0x57E0, 0x57F4,
	0x57F7, 0x57F9, 0x57FA, 0x57FC, 0x5800, 0x5802, 0x5805, 0x5806, 0x5815,
	0x5824, 0x582A, 0x5830, 0x5831, 0x5834, 0x5835, 0x583A, 0x5840, 0x5841,
	0x584A, 0x5851, 0x5854, 0x5857, 0x5858, 0x5859, 0x585A, 0x585E, 0x5869,
	0x586B, 0x5875, 0x587E, 0x5883, 0x5893, 0x5897, 0x589C, 0x58A8, 0x58B3,
	0x58BE, 0x58C1, 0x58C7, 0x58CA, 0x58CC, 0x58D5, 0x58EB, 0x58EC, 0x58EE,
	0x58F0, 0x58F1, 0x58F2, 0x58F7, 0x5909, 0x590F, 0x5915, 0x5916, 0x5919,
	0x591A, 0x591C, 0x5922, 0x5927, 0x5929, 0x592A, 0x592B, 0x592E, 0x5931,
	0x5937, 0x5944, 0x5947, 0x5948, 0x5949, 0x594F, 0x5951, 0x5954, 0x5957,
	0x5965, 0x5968, 0x596A, 0x596E, 0x5973, 0x5974, 0x597D, 0x5982, 0x5983,
	0x5984, 0x598A, 0x5993, 0x5996, 0x5999, 0x59A5, 0x59A8, 0x59AC, 0x59B9,
	0x59BB, 0x59BE, 0x59C9, 0x59CB, 0x59D0, 0x59D1, 0x59D3, 0x59D4, 0x59E5,
	0x59E6, 0x59EA, 0x59EB, 0x59F6, 0x59FB, 0x59FF, 0x5A01, 0x5A03, 0x5A18,
	0x5A20, 0x5A29, 0x5A2F, 0x5A3C, 0x5A41, 0x5A46, 0x5A5A, 0x5A66, 0x5A7F,
	0x5A92, 0x5A9B, 0x5AC1, 0x5AC9, 0x5ACC, 0x5AE1, 0x5B09, 0x5B22, 0x5B2C,
	0x5B30, 0x5B50, 0x5B54, 0x5B57, 0x5B58, 0x5B5C, 0x5B5D, 0x5B5F, 0x5B63,
	0x5B64, 0x5B66, 0x5B6B, 0x5B85, 0x5B87, 0x5B88, 0x5B89, 0x5B8B, 0x5B8C,
	0x5B8D, 0x5B8F, 0x5B95, 0x5B97, 0x5B98, 0x5B99, 0x5B9A, 0x5B9B, 0x5B9C,
	0x5B9D, 0x5B9F, 0x5BA2, 0x5BA3, 0x5BA4, 0x5BA5, 0x5BAE, 0x5BB0, 0x5BB3,
	0x5BB4, 0x5BB5, 0x5BB6, 0x5BB9, 0x5BBF, 0x5BC2, 0x5BC4, 0x5BC5, 0x5BC6,
	0x5BCC, 0x5BD2, 0x5BD3, 0x5BDB, 0x5BDD, 0x5BDF, 0x5BE1, 0x5BE7, 0x5BE9,
	0x5BEE, 0x5BF5, 0x5BF8, 0x5BFA, 0x5BFE, 0x5BFF, 0x5C01, 0x5C02, 0x5C04,
	0x5C06, 0x5C09, 0x5C0A, 0x5C0B, 0x5C0E, 0x5C0F, 0x5C11, 0x5C16, 0x5C1A,
	0x5C24, 0x5C2D, 0x5C31, 0x5C3A, 0x5C3B, 0x5C3C, 0x5C3D, 0x5C3E, 0x5C3F,
	0x5C40, 0x5C45, 0x5C48, 0x5C4A, 0x5C4B, 0x5C4D, 0x5C51, 0x5C55, 0x5C5E,
	0x5C60, 0x5C61, 0x5C64, 0x5C65, 0x5C6F, 0x5C71, 0x5C90, 0x5CA1, 0x5CA8,
	0x5CA9, 0x5CAC, 0x5CB1, 0x5CB3, 0x5CB8, 0x5CE0, 0x5CE1, 0x5CE8, 0x5CEF,
	0x5CF0, 0x5CF6, 0x5CFB, 0x5D07, 0x5D0E, 0x5D16, 0x5D29, 0x5D50, 0x5D69,
	0x5D6F, 0x5D8B, 0x5DBA, 0x5DCC, 0x5DDD, 0x5DDE, 0x5DE1, 0x5DE3, 0x5DE5,
	0x5DE6, 0x5DE7, 0x5DE8, 0x5DEE, 0x5DF1, 0x5DF3, 0x5DF4, 0x5DF7, 0x5DFB,
	0x5DFD, 0x5DFE, 0x5E02, 0x5E03, 0x5E06, 0x5E0C, 0x5E16, 0x5E1D, 0x5E25,
	0x5E2B, 0x5E2D, 0x5E2F, 0x5E30, 0x5E33, 0x5E38, 0x5E3D, 0x5E45, 0x5E4C,
	0x5E55, 0x5E61, 0x5E63, 0x5E72, 0x5E73, 0x5E74, 0x5E78, 0x5E79, 0x5E7B,
	0x5E7C, 0x5E7D, 0x5E7E, 0x5E81, 0x5E83, 0x5E84, 0x5E87, 0x5E8A, 0x5E8F,
	0x5E95, 0x5E96, 0x5E97, 0x5E9A, 0x5E9C, 0x5EA6, 0x5EA7, 0x5EAB, 0x5EAD,
	0x5EB5, 0x5EB6, 0x5EB7, 0x5EB8, 0x5EC3, 0x5EC9, 0x5ECA, 0x5ED3, 0x5EDF,
	0x5EE0, 0x5EF6, 0x5EF7, 0x5EFA, 0x5EFB, 0x5EFC, 0x5EFF, 0x5F01, 0x5F04,
	0x5F0A, 0x5F0F, 0x5F10, 0x5F13, 0x5F14, 0x5F15, 0x5F17, 0x5F18, 0x5F1B,
	0x5F1F, 0x5F25, 0x5F26, 0x5F27, 0x5F31, 0x5F35, 0x5F37, 0x5F3C, 0x5F3E,
	0x5F4A, 0x5F53, 0x5F62, 0x5F66, 0x5F69, 0x5F6A, 0x5F6B, 0x5F6C, 0x5F70,
	0x5F71, 0x5F79, 0x5F7C, 0x5F80, 0x5F81, 0x5F84, 0x5F85, 0x5F8B, 0x5F8C,
	0x5F90, 0x5F92, 0x5F93, 0x5F97, 0x5FA1, 0x5FA9, 0x5FAA, 0x5FAE, 0x5FB3,
	0x5FB4, 0x5FB9, 0x5FBD, 0x5FC3, 0x5FC5, 0x5FCC, 0x5FCD, 0x5FD7, 0x5FD8,
	0x5FD9, 0x5FDC, 0x5FE0, 0x5FEB, 0x5FF5, 0x5FFD, 0x6012, 0x6016, 0x601C,
	0x601D, 0x6020, 0x6025, 0x6027, 0x6028, 0x602A, 0x602F, 0x604B, 0x6050,
	0x6052, 0x6055, 0x6062, 0x6065, 0x6068, 0x6069, 0x606D, 0x606F, 0x6070,
	0x6075, 0x6089, 0x608C, 0x6094, 0x609F, 0x60A0, 0x60A3, 0x60A6, 0x60A9,
	0x60AA, 0x60B2, 0x60B6, 0x60BC, 0x60C5, 0x60C7, 0x60D1, 0x60DA, 0x60DC,
	0x60DF, 0x60E3, 0x60E8, 0x60F0, 0x60F3, 0x60F9, 0x6101, 0x6108, 0x6109,
	0x610F, 0x611A, 0x611B, 0x611F, 0x6148, 0x614B, 0x614C, 0x614E, 0x6155,
	0x6162, 0x6163, 0x6167, 0x6168, 0x616E, 0x6170, 0x6176, 0x617E, 0x6182,
	0x618E, 0x6190, 0x61A4, 0x61A7, 0x61A9, 0x61B2, 0x61B6, 0x61BE, 0x61C7,
	0x61D0, 0x61F2, 0x61F8, 0x620A, 0x620E, 0x6210, 0x6211, 0x6212, 0x6216,
	0x621A, 0x621F, 0x6226, 0x622F, 0x6234, 0x6238, 0x623B, 0x623F, 0x6240,
	0x6247, 0x6249, 0x624B, 0x624D, 0x6253, 0x6255, 0x6258, 0x626E, 0x6271,
	0x6276, 0x6279, 0x627F, 0x6280, 0x6284, 0x628A, 0x6291, 0x6295, 0x6297,
	0x6298, 0x629C, 0x629E, 0x62AB, 0x62B1, 0x62B5, 0x62B9, 0x62BC, 0x62BD,
	0x62C5, 0x62CD, 0x62D0, 0x62D2, 0x62D3, 0x62D8, 0x62D9, 0x62DB, 0x62DD,
	0x62E0, 0x62E1, 0x62EC, 0x62ED, 0x62F3, 0x62F6, 0x62F7, 0x62FE, 0x6301,
	0x6307, 0x6309, 0x6311, 0x6319, 0x631F, 0x6328, 0x632B, 0x632F, 0x633A,
	0x633D, 0x633F, 0x6349, 0x634C, 0x6355, 0x6357, 0x635C, 0x6367, 0x6368,
	0x636E, 0x6372, 0x6377, 0x637A, 0x637B, 0x6383, 0x6388, 0x638C, 0x6392,
	0x6398, 0x639B, 0x63A0, 0x63A1, 0x63A2, 0x63A5, 0x63A7, 0x63A8, 0x63A9,
	0x63AA, 0x63AC, 0x63B2, 0x63B4, 0x63BB, 0x63C3, 0x63CF, 0x63D0, 0x63D6,
	0x63DA, 0x63DB, 0x63E1, 0x63EE, 0x63F4, 0x63FA, 0x640D, 0x642C, 0x642D,
	0x643A, 0x643E, 0x6442, 0x6458, 0x6469, 0x6478, 0x647A, 0x6483, 0x6492,
	0x649A, 0x649E, 0x64A4, 0x64AB, 0x64AD, 0x64AE, 0x64B0, 0x64B2, 0x64B9,
	0x64C1, 0x64CD, 0x64E2, 0x64E6, 0x64EC, 0x64FE, 0x652F, 0x6539, 0x653B,
	0x653E, 0x653F, 0x6545, 0x654F, 0x6551, 0x6557, 0x6559, 0x6562, 0x6563,
	0x6566, 0x656C, 0x6570, 0x6574, 0x6575, 0x6577, 0x6587, 0x6589, 0x658C,
	0x658E, 0x6590, 0x6591, 0x6597, 0x6599, 0x659C, 0x65A1, 0x65A4, 0x65A5,
	0x65A7, 0x65AC, 0x65AD, 0x65AF, 0x65B0, 0x65B9, 0x65BC, 0x65BD, 0x65C5,
	0x65CB, 0x65CF, 0x65D7, 0x65E2, 0x65E5, 0x65E6, 0x65E7, 0x65E8, 0x65E9,
	0x65EC, 0x65ED, 0x65FA, 0x6602, 0x6606, 0x6607, 0x660C, 0x660E, 0x660F,
	0x6613, 0x6614, 0x661F, 0x6620, 0x6625, 0x6627, 0x6628, 0x662D, 0x662F,
	0x663C, 0x6642, 0x6643, 0x664B, 0x6652, 0x6666, 0x6669, 0x666E, 0x666F,
	0x6674, 0x6676, 0x667A, 0x6681, 0x6687, 0x6691, 0x6696, 0x6697, 0x66A2,
	0x66A6, 0x66AB, 0x66AE, 0x66B4, 0x66C7, 0x66D9, 0x66DC, 0x66DD, 0x66F2,
	0x66F3, 0x66F4, 0x66F8, 0x66F9, 0x66FD, 0x66FE, 0x66FF, 0x6700, 0x6708,
	0x6709, 0x670B, 0x670D, 0x6714, 0x6715, 0x6717, 0x671B, 0x671D, 0x671F,
	0x6728, 0x672A, 0x672B, 0x672C, 0x672D, 0x6731, 0x6734, 0x673A, 0x673D,
	0x6749, 0x674E, 0x674F, 0x6750, 0x6751, 0x6753, 0x6756, 0x675C, 0x675F,
	0x6761, 0x6762, 0x6765, 0x676D, 0x676F, 0x6771, 0x6775, 0x6777, 0x677E,
	0x677F, 0x6787, 0x6790, 0x6795, 0x6797, 0x679A, 0x679C, 0x679D, 0x67A0,
	0x67A2, 0x67AF, 0x67B6, 0x67C1, 0x67C4, 0x67CA, 0x67CF, 0x67D0, 0x67D1,
	0x67D3, 0x67D4, 0x67D8, 0x67DA, 0x67F1, 0x67F3, 0x67F4, 0x67F5, 0x67FB,
	0x67FE, 0x67FF, 0x6802, 0x6803, 0x6804, 0x6813, 0x6816, 0x6817, 0x6821,
	0x6822, 0x682A, 0x6834, 0x6838, 0x6839, 0x683C, 0x683D, 0x6841, 0x6842,
	0x6843, 0x6848, 0x6850, 0x6851, 0x6853, 0x6854, 0x685C, 0x685D, 0x685F,
	0x6867, 0x6876, 0x6881, 0x6885, 0x6893, 0x6897, 0x68A2, 0x68A7, 0x68A8,
	0x68AF, 0x68B0, 0x68B1, 0x68B6, 0x68BC, 0x68C4, 0x68C9, 0x68CB, 0x68D2,
	0x68DA, 0x68DF, 0x68EE, 0x68F2, 0x68FA, 0x6900, 0x6905, 0x690B, 0x690D,
	0x690E, 0x6919, 0x691B, 0x691C, 0x6934, 0x693F, 0x694A, 0x6953, 0x6955,
	0x695A, 0x6960, 0x6962, 0x696D, 0x696F, 0x6973, 0x6975, 0x697C, 0x697D,
	0x6982, 0x698A, 0x698E, 0x6994, 0x699B, 0x69CB, 0x69CC, 0x69CD, 0x69D8,
	0x69D9, 0x69FB, 0x69FD, 0x6A0B, 0x6A17, 0x6A19, 0x6A1F, 0x6A21, 0x6A29,
	0x6A2A, 0x6A2B, 0x6A35, 0x6A39, 0x6A3A, 0x6A3D, 0x6A4B, 0x6A58, 0x6A5F,
	0x6A61, 0x6A7F, 0x6A80, 0x6A8E, 0x6AD3, 0x6ADB, 0x6AE8, 0x6B04, 0x6B1D,
	0x6B20, 0x6B21, 0x6B23, 0x6B27, 0x6B32, 0x6B3A, 0x6B3D, 0x6B3E, 0x6B4C,
	0x6B4E, 0x6B53, 0x6B62, 0x6B63, 0x6B64, 0x6B66, 0x6B69, 0x6B6A, 0x6B6F,
	0x6B73, 0x6B74, 0x6B7B, 0x6B86, 0x6B89, 0x6B8A, 0x6B8B, 0x6B96, 0x6BB4,
	0x6BB5, 0x6BBA, 0x6BBB, 0x6BBF, 0x6BC5, 0x6BCD, 0x6BCE, 0x6BD2, 0x6BD4,
	0x6BD8, 0x6BDB, 0x6C0F, 0x6C11, 0x6C17, 0x6C34, 0x6C37, 0x6C38, 0x6C3E,
	0x6C40, 0x6C41, 0x6C42, 0x6C4E, 0x6C50, 0x6C57, 0x6C5A, 0x6C5D, 0x6C5F,
	0x6C60, 0x6C70, 0x6C72, 0x6C7A, 0x6C7D, 0x6C83, 0x6C88, 0x6C8C, 0x6C93,
	0x6C96, 0x6C99, 0x6CA1, 0x6CA2, 0x6CAB, 0x6CB3, 0x6CB8, 0x6CB9, 0x6CBB,
	0x6CBC, 0x6CBF, 0x6CC1, 0x6CC9, 0x6CCA, 0x6CCC, 0x6CD5, 0x6CE1, 0x6CE2,
	0x6CE3, 0x6CE5, 0x6CE8, 0x6CF0, 0x6CF3, 0x6D0B, 0x6D17, 0x6D1B, 0x6D1E,
	0x6D25, 0x6D29, 0x6D2A, 0x6D32, 0x6D3B, 0x6D3E, 0x6D41, 0x6D44, 0x6D45,
	0x6D5C, 0x6D66, 0x6D69, 0x6D6A, 0x6D6C, 0x6D6E, 0x6D74, 0x6D77, 0x6D78,
	0x6D88, 0x6D8C, 0x6D99, 0x6D9B, 0x6D9C, 0x6DAF, 0x6DB2, 0x6DBC, 0x6DC0,
	0x6DCB, 0x6DD1, 0x6DD8, 0x6DE1, 0x6DEB, 0x6DF1, 0x6DF3, 0x6DF5, 0x6DF7,
	0x6DFB, 0x6E05, 0x6E07, 0x6E08, 0x6E09, 0x6E0B, 0x6E13, 0x6E1A, 0x6E1B,
	0x6E20, 0x6E21, 0x6E25, 0x6E26, 0x6E29, 0x6E2C, 0x6E2F, 0x6E4A, 0x6E56,
	0x6E58, 0x6E5B, 0x6E67, 0x6E6F, 0x6E7E, 0x6E7F, 0x6E80, 0x6E8C, 0x6E90,
	0x6E96, 0x6E9C, 0x6E9D, 0x6EA2, 0x6EB6, 0x6EBA, 0x6EC5, 0x6ECB, 0x6ED1,
	0x6EDD, 0x6EDE, 0x6EF4, 0x6F01, 0x6F02, 0x6F06, 0x6F09, 0x6F0F, 0x6F14,
	0x6F15, 0x6F20, 0x6F22, 0x6F23, 0x6F2B, 0x6F2C, 0x6F38, 0x6F45, 0x6F54,
	0x6F5C, 0x6F5F, 0x6F64, 0x6F6E, 0x6F70, 0x6F84, 0x6F97, 0x6FB1, 0x6FC0,
	0x6FC1, 0x6FC3, 0x6FE0, 0x6FE1, 0x6FEB, 0x6FEF, 0x7015, 0x701E, 0x7026,
	0x7027, 0x702C, 0x7058, 0x706B, 0x706F, 0x7070, 0x7078, 0x707C, 0x707D,
	0x7089, 0x708A, 0x708E, 0x70AD, 0x70B9, 0x70BA, 0x70C8, 0x70CF, 0x70F9,
	0x7114, 0x711A, 0x7121, 0x7126, 0x7136, 0x713C, 0x7149, 0x714E, 0x7159,
	0x7164, 0x7167, 0x7169, 0x716E, 0x717D, 0x718A, 0x7194, 0x719F, 0x71B1,
	0x71C3, 0x71C8, 0x71D0, 0x71D5, 0x71E5, 0x71E6, 0x71ED, 0x7206, 0x722A,
	0x7235, 0x7236, 0x723A, 0x723D, 0x723E, 0x7247, 0x7248, 0x724C, 0x7252,
	0x7259, 0x725B, 0x725D, 0x725F, 0x7261, 0x7262, 0x7267, 0x7269, 0x7272,
	0x7279, 0x727D, 0x7280, 0x72A0, 0x72AC, 0x72AF, 0x72B6, 0x72C2, 0x72D0,
	0x72D7, 0x72D9, 0x72DB, 0x72E9, 0x72EC, 0x72ED, 0x72F8, 0x72FC, 0x72FD,
	0x731B, 0x731F, 0x732A, 0x732B, 0x732E, 0x7336, 0x7337, 0x733F, 0x7344,
	0x7345, 0x7363, 0x7372, 0x7384, 0x7387, 0x7389, 0x738B, 0x7396, 0x73A9,
	0x73B2, 0x73C2, 0x73CA, 0x73CD, 0x73E0, 0x73EA, 0x73ED, 0x73FE, 0x7403,
	0x7406, 0x7409, 0x7422, 0x7433, 0x7434, 0x7435, 0x7436, 0x745A, 0x745B,
	0x745E, 0x7460, 0x7473, 0x7483, 0x74B0, 0x74BD, 0x74DC, 0x74E2, 0x74E6,
	0x74F6, 0x7511, 0x7518, 0x751A, 0x751C, 0x751F, 0x7523, 0x7525, 0x7528,
	0x752B, 0x7530, 0x7531, 0x7532, 0x7533, 0x7537, 0x753A, 0x753B, 0x754C,
	0x754F, 0x7551, 0x7554, 0x7559, 0x755C, 0x755D, 0x7560, 0x7562, 0x7565,
	0x7566, 0x756A, 0x7570, 0x7573, 0x7577, 0x757F, 0x758B, 0x758E, 0x758F,
	0x7591, 0x75AB, 0x75B2, 0x75B9, 0x75BE, 0x75C5, 0x75C7, 0x75D4, 0x75D5,
	0x75D8, 0x75DB, 0x75E2, 0x75E9, 0x75F4, 0x7642, 0x764C, 0x7652, 0x7656,
	0x767A, 0x767B, 0x767D, 0x767E, 0x7684, 0x7686, 0x7687, 0x7690, 0x76AE,
	0x76BF, 0x76C3, 0x76C6, 0x76C8, 0x76CA, 0x76D7, 0x76DB, 0x76DF, 0x76E3,
	0x76E4, 0x76EE, 0x76F2, 0x76F4, 0x76F8, 0x76FE, 0x7701, 0x7709, 0x770B,
	0x770C, 0x771F, 0x7720, 0x773A, 0x773C, 0x7740, 0x7761, 0x7763, 0x7766,
	0x77A5, 0x77AC, 0x77AD, 0x77B3, 0x77DB, 0x77E2, 0x77E5, 0x77E7, 0x77E9,
	0x77ED, 0x77EF, 0x77F3, 0x7802, 0x7814, 0x7815, 0x7825, 0x7826, 0x7827,
	0x7832, 0x7834, 0x783A, 0x783F, 0x785D, 0x786B, 0x786C, 0x786F, 0x7872,
	0x7881, 0x7887, 0x788D, 0x7891, 0x7893, 0x7895, 0x7897, 0x78A7, 0x78A9,
	0x78BA, 0x78C1, 0x78D0, 0x78E8, 0x78EF, 0x7901, 0x790E, 0x793A, 0x793C,
	0x793E, 0x7941, 0x7947, 0x7948, 0x7949, 0x7950, 0x7956, 0x795D, 0x795E,
	0x7962, 0x7965, 0x7968, 0x796D, 0x7977, 0x7981, 0x7984, 0x7985, 0x798D,
	0x798E, 0x798F, 0x79A6, 0x79B0, 0x79BD, 0x79BE, 0x79BF, 0x79C0, 0x79C1,
	0x79CB, 0x79D1, 0x79D2, 0x79D8, 0x79DF, 0x79E4, 0x79E6, 0x79E9, 0x79F0,
	0x79FB, 0x7A00, 0x7A0B, 0x7A0E, 0x7A14, 0x7A17, 0x7A1A, 0x7A1C, 0x7A2E,
	0x7A32, 0x7A3C, 0x7A3D, 0x7A3F, 0x7A40, 0x7A42, 0x7A46, 0x7A4D, 0x7A4E,
	0x7A4F, 0x7A50, 0x7A63, 0x7A6B, 0x7A74, 0x7A76, 0x7A7A, 0x7A7F, 0x7A81,
	0x7A83, 0x7A84, 0x7A92, 0x7A93, 0x7A9F, 0x7AAA, 0x7AAE, 0x7AAF, 0x7ABA,
	0x7AC3, 0x7ACB, 0x7ADC, 0x7AE0, 0x7AE3, 0x7AE5, 0x7AEA, 0x7AEF, 0x7AF6,
	0x7AF9, 0x7AFA, 0x7AFF, 0x7B08, 0x7B11, 0x7B1B, 0x7B20, 0x7B25, 0x7B26,
	0x7B2C, 0x7B39, 0x7B46, 0x7B48, 0x7B49, 0x7B4B, 0x7B4F, 0x7B51, 0x7B52,
	0x7B54, 0x7B56, 0x7B86, 0x7B87, 0x7B94, 0x7B95, 0x7B97, 0x7BA1, 0x7BAA,
	0x7BAD, 0x7BB1, 0x7BB8, 0x7BC0, 0x7BC4, 0x7BC7, 0x7BC9, 0x7BE0, 0x7BE4,
	0x7BED, 0x7C21, 0x7C38, 0x7C3E, 0x7C3F, 0x7C4D, 0x7C73, 0x7C7E, 0x7C81,
	0x7C82, 0x7C89, 0x7C8B, 0x7C8D, 0x7C92, 0x7C95, 0x7C97, 0x7C98, 0x7C9B,
	0x7C9F, 0x7CA5, 0x7CA7, 0x7CBE, 0x7CCA, 0x7CCE, 0x7CD6, 0x7CDE, 0x7CDF,
	0x7CE0, 0x7CE7, 0x7CF8, 0x7CFB, 0x7CFE, 0x7D00, 0x7D04, 0x7D05, 0x7D0B,
	0x7D0D, 0x7D10, 0x7D14, 0x7D17, 0x7D18, 0x7D19, 0x7D1A, 0x7D1B, 0x7D20,
	0x7D21, 0x7D22, 0x7D2B, 0x7D2C, 0x7D2F, 0x7D30, 0x7D33, 0x7D39, 0x7D3A,
	0x7D42, 0x7D43, 0x7D44, 0x7D4C, 0x7D50, 0x7D5E, 0x7D61, 0x7D62, 0x7D66,
	0x7D71, 0x7D75, 0x7D76, 0x7D79, 0x7D99, 0x7D9A, 0x7D9C, 0x7DAC, 0x7DAD,
	0x7DB1, 0x7DB2, 0x7DB4, 0x7DBB, 0x7DBE, 0x7DBF, 0x7DCA, 0x7DCB, 0x7DCF,
	0x7DD1, 0x7DD2, 0x7DDA, 0x7DE0, 0x7DE8, 0x7DE9, 0x7DEC, 0x7DEF, 0x7DF4,
	0x7E01, 0x7E04, 0x7E1B, 0x7E1E, 0x7E26, 0x7E2B, 0x7E2E, 0x7E3E, 0x7E41,
	0x7E4A, 0x7E4B, 0x7E4D, 0x7E54, 0x7E55, 0x7E6D, 0x7E70, 0x7E82, 0x7E8F,
	0x7F36, 0x7F6A, 0x7F6B, 0x7F6E, 0x7F70, 0x7F72, 0x7F75, 0x7F77, 0x7F85,
	0x7F8A, 0x7F8E, 0x7FA4, 0x7FA8, 0x7FA9, 0x7FBD, 0x7FC1, 0x7FCC, 0x7FD2,
	0x7FE0, 0x7FEB, 0x7FF0, 0x7FFB, 0x7FFC, 0x8000, 0x8001, 0x8003, 0x8005,
	0x800C, 0x8010, 0x8015, 0x8017, 0x8033, 0x8036, 0x803D, 0x8056, 0x805E,
	0x8061, 0x806F, 0x8074, 0x8077, 0x807E, 0x8087, 0x8089, 0x808B, 0x808C,
	0x8096, 0x8098, 0x809D, 0x80A1, 0x80A2, 0x80A5, 0x80A9, 0x80AA, 0x80AF,
	0x80B1, 0x80B2, 0x80B4, 0x80BA, 0x80C3, 0x80C6, 0x80CC, 0x80CE, 0x80DE,
	0x80E1, 0x80E4, 0x80F4, 0x80F8, 0x80FD, 0x8102, 0x8105, 0x8106, 0x8107,
	0x8108, 0x810A, 0x811A, 0x8131, 0x8133, 0x8139, 0x814E, 0x8150, 0x8154,
	0x8155, 0x816B, 0x8170, 0x8178, 0x8179, 0x817A, 0x817F, 0x818F, 0x819A,
	0x819C, 0x819D, 0x81A8, 0x81B3, 0x81BF, 0x81C6, 0x81D3, 0x81E3, 0x81E5,
	0x81E8, 0x81EA, 0x81ED, 0x81F3, 0x81F4, 0x81FC, 0x8208, 0x820C, 0x820E,
	0x8217, 0x8218, 0x821B, 0x821C, 0x821E, 0x821F, 0x822A, 0x822C, 0x8235,
	0x8236, 0x8237, 0x8239, 0x8247, 0x8266, 0x826E, 0x826F, 0x8272, 0x8276,
	0x828B, 0x8299, 0x829D, 0x82A5, 0x82A6, 0x82AD, 0x82AF, 0x82B1, 0x82B3,
	0x82B8, 0x82B9, 0x82BD, 0x82C5, 0x82D1, 0x82D3, 0x82D4, 0x82D7, 0x82DB,
	0x82E5, 0x82E6, 0x82E7, 0x82EB, 0x82F1, 0x8302, 0x8304, 0x8305, 0x830E,
	0x831C, 0x8328, 0x8336, 0x8338, 0x8349, 0x834A, 0x834F, 0x8352, 0x8358,
	0x8377, 0x837B, 0x839E, 0x83AB, 0x83B1, 0x83C5, 0x83CA, 0x83CC, 0x83D3,
	0x83D6, 0x83DC, 0x83DF, 0x83E9, 0x83EF, 0x83F0, 0x83F1, 0x8404, 0x840C,
	0x840E, 0x8429, 0x8431, 0x843D, 0x8449, 0x844E, 0x8457, 0x845B, 0x8461,
	0x8463, 0x8466, 0x846C, 0x8471, 0x8475, 0x847A, 0x848B, 0x8490, 0x8494,
	0x8499, 0x849C, 0x84B2, 0x84B8, 0x84BC, 0x84C4, 0x84C9, 0x84CB, 0x84D1,
	0x84EC, 0x84EE, 0x8500, 0x8511, 0x8513, 0x851A, 0x8526, 0x852D, 0x8535,
	0x853D, 0x8543, 0x8549, 0x854A, 0x854E, 0x8557, 0x8568, 0x8569, 0x856A,
	0x8584, 0x8597, 0x8599, 0x85A6, 0x85A9, 0x85AA, 0x85AB, 0x85AC, 0x85AE,
	0x85AF, 0x85C1, 0x85CD, 0x85E4, 0x85E9, 0x85F7, 0x85FB, 0x8607, 0x862D,
	0x864E, 0x8650, 0x865A, 0x865C, 0x865E, 0x866B, 0x8679, 0x867B, 0x868A,
	0x8695, 0x86A4, 0x86C7, 0x86CB, 0x86CD, 0x86CE, 0x86D9, 0x86E4, 0x86ED,
	0x86EE, 0x86F8, 0x86FE, 0x8702, 0x8718, 0x871C, 0x8749, 0x874B, 0x8755,
	0x8766, 0x8776, 0x877F, 0x878D, 0x87BA, 0x87F9, 0x87FB, 0x8840, 0x8846,
	0x884C, 0x8853, 0x8857, 0x885B, 0x885D, 0x8861, 0x8863, 0x8868, 0x8870,
	0x8877, 0x887F, 0x8888, 0x888B, 0x8896, 0x88AB, 0x88B4, 0x88B7, 0x88C1,
	0x88C2, 0x88C5, 0x88CF, 0x88D5, 0x88DC, 0x88DF, 0x88E1, 0x88F3, 0x88F8,
	0x88FD, 0x88FE, 0x8907, 0x8910, 0x8912, 0x8956, 0x895F, 0x8972, 0x897F,
	0x8981, 0x8986, 0x8987, 0x898B, 0x898F, 0x8996, 0x8997, 0x899A, 0x89A7,
	0x89AA, 0x89B3, 0x89D2, 0x89E3, 0x89E6, 0x8A00, 0x8A02, 0x8A08, 0x8A0A,
	0x8A0E, 0x8A13, 0x8A17, 0x8A18, 0x8A1F, 0x8A23, 0x8A2A, 0x8A2D, 0x8A31,
	0x8A33, 0x8A34, 0x8A3A, 0x8A3B, 0x8A3C, 0x8A50, 0x8A51, 0x8A54, 0x8A55,
	0x8A5E, 0x8A60, 0x8A63, 0x8A66, 0x8A69, 0x8A6B, 0x8A6E, 0x8A70, 0x8A71,
	0x8A72, 0x8A73, 0x8A87, 0x8A89, 0x8A8C, 0x8A8D, 0x8A93, 0x8A95, 0x8A98,
	0x8A9E, 0x8AA0, 0x8AA4, 0x8AAC, 0x8AAD, 0x8AB0, 0x8AB2, 0x8AB9, 0x8ABC,
	0x8ABF, 0x8AC7, 0x8ACB, 0x8ACC, 0x8ACF, 0x8AD2, 0x8AD6, 0x8ADC, 0x8AE6,
	0x8AED, 0x8AEE, 0x8AF8, 0x8AFA, 0x8AFE, 0x8B00, 0x8B01, 0x8B02, 0x8B04,
	0x8B0E, 0x8B19, 0x8B1B, 0x8B1D, 0x8B21, 0x8B2C, 0x8B39, 0x8B58, 0x8B5C,
	0x8B66, 0x8B70, 0x8B72, 0x8B77, 0x8B83, 0x8B90, 0x8C37, 0x8C46, 0x8C4A,
	0x8C5A, 0x8C61, 0x8C6A, 0x8C79, 0x8C8C, 0x8C9D, 0x8C9E, 0x8CA0, 0x8CA1,
	0x8CA2, 0x8CA7, 0x8CA8, 0x8CA9, 0x8CAB, 0x8CAC, 0x8CAF, 0x8CB0, 0x8CB4,
	0x8CB7, 0x8CB8, 0x8CBB, 0x8CBC, 0x8CBF, 0x8CC0, 0x8CC2, 0x8CC3, 0x8CC4,
	0x8CC7, 0x8CCA, 0x8CCE, 0x8CD1, 0x8CD3, 0x8CDB, 0x8CDC, 0x8CDE, 0x8CE0,
	0x8CE2, 0x8CE6, 0x8CEA, 0x8CED, 0x8CFC, 0x8D08, 0x8D0B, 0x8D64, 0x8D66,
	0x8D6B, 0x8D70, 0x8D74, 0x8D77, 0x8D85, 0x8D8A, 0x8DA3, 0x8DA8, 0x8DB3,
	0x8DDD, 0x8DE1, 0x8DE8, 0x8DEF, 0x8DF3, 0x8DF5, 0x8E0A, 0x8E0F, 0x8E44,
	0x8E5F, 0x8E74, 0x8E8D, 0x8EAB, 0x8EAF, 0x8ECA, 0x8ECC, 0x8ECD, 0x8ED2,
	0x8EDF, 0x8EE2, 0x8EF8, 0x8EFD, 0x8F03, 0x8F09, 0x8F14, 0x8F1D, 0x8F29,
	0x8F2A, 0x8F2F, 0x8F38, 0x8F3F, 0x8F44, 0x8F4D, 0x8F5F, 0x8F61, 0x8F9B,
	0x8F9E, 0x8FB0, 0x8FB1, 0x8FB2, 0x8FBA, 0x8FBB, 0x8FBC, 0x8FBF, 0x8FC2,
	0x8FC4, 0x8FC5, 0x8FCE, 0x8FD1, 0x8FD4, 0x8FE6, 0x8FE9, 0x8FEB, 0x8FED,
	0x8FF0, 0x8FF7, 0x8FFD, 0x9000, 0x9001, 0x9003, 0x9006, 0x900F, 0x9010,
	0x9013, 0x9014, 0x9017, 0x9019, 0x901A, 0x901D, 0x901F, 0x9020, 0x9022,
	0x9023, 0x902E, 0x9031, 0x9032, 0x9038, 0x903C, 0x9041, 0x9042, 0x9045,
	0x9047, 0x904A, 0x904B, 0x904D, 0x904E, 0x9053, 0x9054, 0x9055, 0x905C,
	0x9060, 0x9061, 0x9063, 0x9065, 0x9069, 0x906D, 0x906E, 0x9075, 0x9077,
	0x9078, 0x907A, 0x907C, 0x907F, 0x9084, 0x9091, 0x90A3, 0x90A6, 0x90AA,
	0x90B8, 0x90C1, 0x90CA, 0x90CE, 0x90E1, 0x90E8, 0x90ED, 0x90F5, 0x90F7,
	0x90FD, 0x912D, 0x9149, 0x914B, 0x914C, 0x914D, 0x914E, 0x9152, 0x9154,
	0x9162, 0x916A, 0x916C, 0x9175, 0x9177, 0x9178, 0x9187, 0x918D, 0x9190,
	0x9192, 0x9197, 0x919C, 0x91A4, 0x91B8, 0x91C6, 0x91C7, 0x91C8, 0x91CC,
	0x91CD, 0x91CE, 0x91CF, 0x91D1, 0x91D8, 0x91DC, 0x91DD, 0x91E3, 0x91E6,
	0x91E7, 0x920D, 0x920E, 0x9234, 0x9237, 0x9244, 0x925B, 0x9262, 0x9266,
	0x9271, 0x927E, 0x9280, 0x9283, 0x9285, 0x9291, 0x9298, 0x929A, 0x92AD,
	0x92D2, 0x92E4, 0x92EA, 0x92ED, 0x92F2, 0x92F3, 0x92F8, 0x92FC, 0x9306,
	0x9310, 0x9318, 0x9320, 0x9326, 0x9328, 0x932B, 0x932C, 0x932F, 0x9332,
	0x934B, 0x934D, 0x9354, 0x935B, 0x936C, 0x9375, 0x937E, 0x938C, 0x9396,
	0x9397, 0x939A, 0x93A7, 0x93AE, 0x93D1, 0x93E1, 0x9418, 0x9419, 0x9438,
	0x9451, 0x9453, 0x9577, 0x9580, 0x9583, 0x9589, 0x958B, 0x958F, 0x9591,
	0x9593, 0x95A2, 0x95A3, 0x95A4, 0x95A5, 0x95B2, 0x95C7, 0x95D8, 0x961C,
	0x962A, 0x9632, 0x963B, 0x963F, 0x9640, 0x9644, 0x964D, 0x9650, 0x965B,
	0x9662, 0x9663, 0x9664, 0x9665, 0x966A, 0x9670, 0x9673, 0x9675, 0x9676,
	0x9678, 0x967A, 0x967D, 0x9685, 0x9686, 0x9688, 0x968A, 0x968E, 0x968F,
	0x9694, 0x9699, 0x969B, 0x969C, 0x96A0, 0x96A3, 0x96B7, 0x96BB, 0x96BC,
	0x96C0, 0x96C1, 0x96C4, 0x96C5, 0x96C6, 0x96C7, 0x96CC, 0x96D1, 0x96DB,
	0x96E2, 0x96E3, 0x96E8, 0x96EA, 0x96EB, 0x96F0, 0x96F2, 0x96F6, 0x96F7,
	0x96FB, 0x9700, 0x9707, 0x970A, 0x971C, 0x971E, 0x9727, 0x9732, 0x9752,
	0x9756, 0x9759, 0x975E, 0x9762, 0x9769, 0x976D, 0x9774, 0x9784, 0x978D,
	0x9798, 0x97A0, 0x97AD, 0x97D3, 0x97EE, 0x97F3, 0x97FB, 0x97FF, 0x9801,
	0x9802, 0x9803, 0x9805, 0x9806, 0x9808, 0x9810, 0x9811, 0x9812, 0x9813,
	0x9817, 0x9818, 0x981A, 0x982C, 0x982D, 0x9834, 0x983B, 0x983C, 0x984C,
	0x984D, 0x984E, 0x9854, 0x9855, 0x9858, 0x985B, 0x985E, 0x9867, 0x98A8,
	0x98DB, 0x98DF, 0x98E2, 0x98EF, 0x98F2, 0x98F4, 0x98FC, 0x98FD, 0x98FE,
	0x9905, 0x990A, 0x990C, 0x9910, 0x9913, 0x9928, 0x9957, 0x9996, 0x9999,
	0x99A8, 0x99AC, 0x99B3, 0x99B4, 0x99C1, 0x99C4, 0x99C5, 0x99C6, 0x99C8,
	0x99D0, 0x99D2, 0x99D5, 0x99FF, 0x9A0E, 0x9A12, 0x9A13, 0x9A28, 0x9A30,
	0x9A5A, 0x9AA8, 0x9AB8, 0x9AC4, 0x9AD8, 0x9AEA, 0x9AED, 0x9B3C, 0x9B41,
	0x9B42, 0x9B45, 0x9B54, 0x9B5A, 0x9B6F, 0x9B8E, 0x9B92, 0x9BAA, 0x9BAB,
	0x9BAD, 0x9BAE, 0x9BC9, 0x9BD6, 0x9BDB, 0x9BE8, 0x9BF5, 0x9C0D, 0x9C10,
	0x9C2D, 0x9C2F, 0x9C39, 0x9C3B, 0x9C48, 0x9C52, 0x9C57, 0x9CE5, 0x9CE9,
	0x9CF3, 0x9CF4, 0x9CF6, 0x9D07, 0x9D0E, 0x9D1B, 0x9D28, 0x9D2B, 0x9D2C,
	0x9D3B, 0x9D5C, 0x9D60, 0x9D61, 0x9D6C, 0x9D8F, 0x9DB4, 0x9DF2, 0x9DF9,
	0x9DFA, 0x9E78, 0x9E7F, 0x9E93, 0x9E97, 0x9E9F, 0x9EA6, 0x9EB9, 0x9EBA,
	0x9EBB, 0x9EBF, 0x9EC4, 0x9ECD, 0x9ED2, 0x9ED9, 0x9EDB, 0x9F0E, 0x9F13,
	0x9F20, 0x9F3B, 0x9F62, 0x9F8D,
);

# initialize
@ary = ();
for ($i = 0; $i < 8192; $i++) {
	$ary[$i] = 0;
}
# map @char_range
$size = (@char_range);
for ($i = 0; $i < $size; $i += 2) {
	for ($code = $char_range[$i]; $code <= $char_range[$i + 1]; $code++) {
		$ary[$code >> 3] |= 1 << ($code % 8);
	}
}
# map @kanji
foreach $code(@kanji) {
	$ary[$code >> 3] |= 1 << ($code % 8);
}

# output
open(OUT, '>../res/raw/codes.bin') || die 'error!';
binmode OUT;
for ($i = 0; $i < 8192; $i++) {
	print OUT pack('c', $ary[$i]);
}
close OUT;


exit;


