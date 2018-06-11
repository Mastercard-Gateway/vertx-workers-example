# coding: utf-8
require 'date'
require 'net/http'
require 'uri'
require 'json'

uri = URI.parse("http://localhost:8085/coffee")
header = {'Content-Type': 'application/json'}

coffee_types = ['americano', 'latte', 'cappucino', 'mocha']
coffee_size = ['small', 'large', 'medium']
customers = ['aine', 'bernard', 'charline', 'denise', 'esteban', 'françois', 'geraldine', 'herbert']

http = Net::HTTP.new(uri.host, uri.port)

2.times do
  10.times do

    order = {
      customer: customers.sample,
      coffee: coffee_types.sample,
      size: coffee_size.sample
    }

    request = Net::HTTP::Post.new(uri.request_uri, header)
    request.body = order.to_json
    response = http.request(request)
  end
  sleep 2
end
