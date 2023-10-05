 class OfficialFriend extends Recipient{
	String designation;
	String birthday;
	
	public OfficialFriend(String record){
		
		String personal_data = record.split(":")[1].strip();
		String[] data_array = personal_data.split(",");
		
		this.name = data_array[0].strip();
		this.email = data_array[1].strip();
		this.designation = data_array[2].strip();
		this.birthday = data_array[3].strip();
	}
}
