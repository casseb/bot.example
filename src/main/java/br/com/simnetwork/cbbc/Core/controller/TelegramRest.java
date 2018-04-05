package br.com.simnetwork.cbbc.Core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.BotUtils;

import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

@RestController
@CrossOrigin
public class TelegramRest
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private TelegramBot bot = TelegramBotAdapter.build("596370785:AAHA0eG9Zwsswnvx-JNvAAvFsGxOzCl9vy4");
    private BaseResponse baseResponse;
    private SendResponse sendResponse;

    @RequestMapping("/readMessages")
	public void readMessages(@RequestBody String stringRequest) throws InterruptedException {
		log.debug("Recebido via rest o seguinte conteudo {}",stringRequest);
		if(stringRequest != null) {
			Update update = BotUtils.parseUpdate(stringRequest);
			
			if (update.callbackQuery() != null) {

					bot.execute(new SendMessage(update.callbackQuery().message().chat().id(),
							update.callbackQuery().data()));
				} else {
					System.out.println("Recebendo mensagem:" + update.message().text());

					// envio de "Escrevendo" antes de enviar a resposta
					bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));

					// enviando número de contato recebido
					if (update.message().contact() != null) {
						bot.execute(new SendMessage(update.message().chat().id(),
								"Número " + update.message().contact().phoneNumber() + " enviado"));
					} else if (update.message().location() != null) {
						bot.execute(new SendMessage(update.message().chat().id(),
								"Latitude " + update.message().location().latitude() + " enviada"));
					}

					if (update.message().text() != null) {

						// Criação de Keyboard
						if (update.message().text().equals("/keyboard")) {
							bot.execute(new SendMessage(update.message().chat()
									.id(), "Inserindo keyboard").replyMarkup(new ReplyKeyboardMarkup(
											new String[] { "Primeira linha button1", "Primeira linha button2" },
											new String[] { "Segunda linha button1", "Sergunda linha button2" })));

							// Remover keyboard do bot
						} else if (update.message().text().equals("/limparkeyboard")) {
							bot.execute(new SendMessage(update.message().chat().id(), "limpando keyboard")
											.replyMarkup(new ReplyKeyboardRemove()));

							// Pedir contato
						} else if (update.message().text().equals("/pedircontato")) {
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "pedindo contato")
									.replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton[] {
											new KeyboardButton("Fornecer contato").requestContact(true) })));

							// Pedir localização
						} else if (update.message().text().equals("/pedirlocalizacao")) {
							bot.execute(new SendMessage(update.message().chat().id(), "pedindo localização")
											.replyMarkup(new ReplyKeyboardMarkup(
													new KeyboardButton[] { new KeyboardButton("Fornecer localização")
															.requestLocation(true) })));
							// keyboard inline - URL
						} else if (update.message().text().equals("/keyboardInlineUrl")) {
							bot.execute(new SendMessage(update.message().chat().id(), "Inserindo keyboard inline")
											.replyMarkup(new InlineKeyboardMarkup(
													new InlineKeyboardButton[] { new InlineKeyboardButton("url")
															.url("http://www.google.com.br") })));
							// keyboard inline - callBackData
						} else if (update.message().text().equals("/keyboardInlineCallBack")) {
							bot.execute(new SendMessage(update.message().chat().id(), "Inserindo keyboard inline")
											.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[] {
													new InlineKeyboardButton("Texto de apresentação")
															.callbackData("Texto enviado pelo callback")

							})));
						} else if (update.message().text().equals("/updateNormalMessage")) {
							bot.execute(
									new SendMessage(update.message().chat().id(), "Mensgem original aguarde..."));
							new Thread();
							Thread.sleep(2000);
							baseResponse = bot.execute(new EditMessageText(update.message().chat().id(),
									sendResponse.message().messageId(), "Mensagem editada"));

						} else {
							// envio da mensagem de opções
							bot.execute(new SendMessage(update.message().chat().id(),
									"Digite uma das seguintes opções:" + "\n /keyboard" + "\n /limparkeyboard"
											+ "\n /pedircontato" + "\n /pedirlocalizacao" + "\n /keyboardInlineUrl"
											+ "\n /keyboardInlineCallBack" + "\n /updateNormalMessage"));
						}
					}
				}
		}
    }
}
