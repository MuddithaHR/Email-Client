class RecipientFactory {
	
	public Recipient getRecipientObject(String record) {
		
		String str = record.substring(0,8).toUpperCase();
		
		// creating respective objects
		if( str.equalsIgnoreCase("OFFICIAL")) {
			return new Official(record);
		}
		else if (str.equalsIgnoreCase("PERSONAL")) {
			return new PersonalFriend(record);
		}
		else {
			return new OfficialFriend(record);
		}
	}
}
